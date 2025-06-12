-- LightHouseCourse Database Script for SQL Server
-- Create Database
USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'LightHouseCourse')
BEGIN
    ALTER DATABASE LightHouseCourse SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE LightHouseCourse;
END
GO

CREATE DATABASE LightHouseCourse;
GO

USE LightHouseCourse;
GO

-- Table Users
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    Role NVARCHAR(20) NOT NULL, -- 'admin' or 'user' or 'instructor'
    IsActive BIT NOT NULL DEFAULT 1,
    FullName NVARCHAR(100),
    Phone NVARCHAR(20),
    Address NVARCHAR(255),
    Avatar NVARCHAR(255), 
    Token NVARCHAR(255),
    TokenExpires DATETIME,
    AuthProvider NVARCHAR(50) DEFAULT 'local', -- Register with social local, google, facbook
    AuthProviderId NVARCHAR(255) NULL
);
GO

-- Table Categories 
CREATE TABLE Categories (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(255)
);
GO

-- Table Instructors
CREATE TABLE Instructors (
    InstructorID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL UNIQUE,
    Biography NVARCHAR(1000),
    Specialization NVARCHAR(255),
    ApprovalDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);
GO

-- Table Courses 
CREATE TABLE Courses (
    CourseID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    Description NVARCHAR(MAX),
    Price DECIMAL(10, 2) NOT NULL,
    ImageUrl NVARCHAR(255),
    Duration NVARCHAR(50), -- e.g., Based on video duration total
    Level NVARCHAR(20), -- e.g., "Beginner", "Intermediate", "Advanced"
    ApprovalStatus NVARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    SubmissionDate DATETIME,  
    ApprovalDate DATETIME,
    RejectionReason NVARCHAR(1000),
);
GO

-- Table Orders 
CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    OrderDate DATETIME NOT NULL,
    TotalAmount DECIMAL(10, 2) NOT NULL,
    Status NVARCHAR(20) NOT NULL, -- 'pending', 'completed', 'refunded'
    PaymentTransactionID NVARCHAR(100) NULL, -- ID giao dịch từ VNPAY
    PaymentMethod NVARCHAR(20) NULL, -- 'VNPAY'
    PaymentData NVARCHAR(MAX) NULL, -- Lưu trữ dữ liệu phản hồi từ cổng thanh toán dưới dạng JSON
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Table CartItems
CREATE TABLE CartItems (
    CartItemId INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    CourseID INT NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_CartItems_Users FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    CONSTRAINT FK_CartItems_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    CONSTRAINT UQ_CartItems_UserID_CourseID UNIQUE (UserID, CourseID)
);
GO

-- Table CourseCategory
CREATE TABLE CourseCategory (
    CourseID INT,
    CategoryID INT,
    PRIMARY KEY (CourseID, CategoryID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE CASCADE
);
GO

-- Table CourseInstructors
CREATE TABLE CourseInstructors (
    CourseID INT NOT NULL,
    InstructorID INT NOT NULL,
    PRIMARY KEY (CourseID, InstructorID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
    FOREIGN KEY (InstructorID) REFERENCES Instructors(InstructorID)
);
GO

-- Tabla OrderDetails
CREATE TABLE OrderDetails (
    OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NOT NULL,
    CourseID INT NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
GO

-- Table Lessons (Depend on Courses)
CREATE TABLE Lessons (
    LessonID INT IDENTITY(1,1) PRIMARY KEY,
    CourseID INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    OrderIndex INT NOT NULL,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE
);
GO

-- Table Ratings (depend on Courses and Users)
CREATE TABLE Ratings (
    RatingID INT PRIMARY KEY IDENTITY(1,1),
    CourseID INT NOT NULL,
    UserID INT NOT NULL,
    Stars INT NOT NULL CHECK (Stars BETWEEN 1 AND 5),
    Comment NVARCHAR(500),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Ratings_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    CONSTRAINT FK_Ratings_Users FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    CONSTRAINT UQ_Ratings_Course_User UNIQUE (CourseID, UserID)
);
GO

-- Table CourseProgress (depend on Users and Courses)
CREATE TABLE CourseProgress (
    ProgressID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    CourseID INT NOT NULL,
    LastAccessDate DATETIME DEFAULT GETDATE(),
    CompletionPercentage DECIMAL(5,2) DEFAULT 0, -- Percentage of course completed
    IsCompleted BIT DEFAULT 0,
    CONSTRAINT FK_CourseProgress_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_CourseProgress_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
    CONSTRAINT UQ_CourseProgress_UserID_CourseID UNIQUE (UserID, CourseID)
);
GO

-- Table RefundRequests (depend on Orders and Users)
CREATE TABLE RefundRequests (
    RefundID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NOT NULL,
    UserID INT NOT NULL,
    CourseID INT NULL, -- For course-specific refunds, null for full order refunds
    RequestDate DATETIME DEFAULT GETDATE(),
    Status NVARCHAR(20) NOT NULL DEFAULT 'pending', -- 'pending', 'approved', 'rejected'
    RefundAmount DECIMAL(10,2) NOT NULL,
    Reason NVARCHAR(500) NOT NULL,
    ProcessedDate DATETIME NULL,
    AdminMessage NVARCHAR(500) NULL,
    RefundTransactionID NVARCHAR(100) NULL,
    RefundData NVARCHAR(MAX) NULL,
    RefundPercentage INT NOT NULL DEFAULT 80, -- Default refund percentage
    ProcessedBy INT NULL,
    CONSTRAINT FK_RefundRequests_Orders FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    CONSTRAINT FK_RefundRequests_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_RefundRequests_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
    CONSTRAINT FK_RefundRequests_ProcessedBy FOREIGN KEY (ProcessedBy) REFERENCES Users(UserID)
);
GO

-- Table Discussions (depend on Courses, Lessons and Users)
CREATE TABLE Discussions (
    DiscussionID INT IDENTITY(1,1) PRIMARY KEY,
    CourseID INT NOT NULL,
    LessonID INT NULL, -- Có thể liên kết với bài học cụ thể hoặc không
    UserID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    IsResolved BIT DEFAULT 0, -- Đánh dấu câu hỏi đã được giải quyết chưa
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE NO ACTION,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Table Videos (depend on Lessons)
CREATE TABLE Videos (
    VideoID INT IDENTITY(1,1) PRIMARY KEY,
    LessonID INT NOT NULL,
    Title NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX),
    VideoUrl NVARCHAR(255) NOT NULL,
    Duration INT NOT NULL,  -- Duration in seconds
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- Table Materials (depend on Lessons)
CREATE TABLE Materials (
    MaterialID INT IDENTITY(1,1) PRIMARY KEY,
    LessonID INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX),
    Content NVARCHAR(MAX),
    FileUrl NVARCHAR(500),
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- Table Quizzes (depend on Lessons)
CREATE TABLE Quizzes (
    QuizID INT IDENTITY(1,1) PRIMARY KEY,
    LessonID INT NOT NULL, -- Liên kết với bài học
    Title NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX),
    TimeLimit INT NULL, -- Thời gian làm bài (phút), NULL nếu không giới hạn
    PassingScore INT NOT NULL DEFAULT 70, -- Điểm đạt (%)
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- Table LessonProgress (depend on Users and Lessons)
CREATE TABLE LessonProgress (
    ProgressID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    LessonID INT NOT NULL,
    IsCompleted BIT DEFAULT 0,
    CompletionDate DATETIME,
    LastAccessDate DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LessonProgress_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_LessonProgress_Lessons FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID),
    CONSTRAINT UQ_LessonProgress_UserID_LessonID UNIQUE (UserID, LessonID)
);
GO

-- Table PaymentTransactions (depend on Orders and RefundRequests)
CREATE TABLE PaymentTransactions (
    TransactionID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NULL, -- Có thể NULL nếu là giao dịch hoàn tiền
    RefundRequestID INT NULL, -- Có thể NULL nếu là giao dịch thanh toán
    TransactionType NVARCHAR(20) NOT NULL, -- 'payment', 'refund'
    Amount DECIMAL(10, 2) NOT NULL,
    Provider NVARCHAR(20) NOT NULL, -- 'VNPAY'
    ProviderTransactionID NVARCHAR(100) NOT NULL, -- ID giao dịch từ nhà cung cấp
    Status NVARCHAR(20) NOT NULL, -- 'pending', 'completed', 'failed'
    ResponseData NVARCHAR(MAX) NULL, -- Dữ liệu phản hồi từ cổng thanh toán
    RequestData NVARCHAR(MAX) NULL, -- Dữ liệu yêu cầu gửi đến cổng thanh toán
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (RefundRequestID) REFERENCES RefundRequests(RefundID)
);
GO

-- Table DiscussionReplies (depend on Discussions and Users)
CREATE TABLE DiscussionReplies (
    ReplyID INT IDENTITY(1,1) PRIMARY KEY,
    DiscussionID INT NOT NULL,
    UserID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    IsInstructorReply BIT DEFAULT 0, -- Đánh dấu phản hồi từ giảng viên
    IsAcceptedAnswer BIT DEFAULT 0, -- Marks the reply as the accepted answer
    FOREIGN KEY (DiscussionID) REFERENCES Discussions(DiscussionID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Table LessonItems (depend on Lessons)
CREATE TABLE LessonItems (
    LessonItemID INT IDENTITY(1,1) PRIMARY KEY,
    LessonID INT NOT NULL,
    OrderIndex INT NOT NULL,
    ItemType NVARCHAR(10) NOT NULL, -- 'video', 'material', 'quiz'
    ItemID INT NOT NULL,
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE,
    -- Each lesson can only have one item at a specific order position
    CONSTRAINT UQ_LessonItems_Lesson_Order UNIQUE (LessonID, OrderIndex),
    -- Each item (video, material, quiz) can only be used once in the system
    -- Uniquely identifies a concrete item by its type and specific ID
    CONSTRAINT UQ_LessonItems_Item UNIQUE (ItemType, ItemID)
);
GO

-- Table Questions (depend on Quizzes)
CREATE TABLE Questions (
    QuestionID INT IDENTITY(1,1) PRIMARY KEY,
    QuizID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(20) NOT NULL, -- 'multiple_choice', 'true_false'
    Points INT NOT NULL DEFAULT 1, -- Điểm cho câu hỏi
    OrderIndex INT NOT NULL, -- Thứ tự câu hỏi trong bài kiểm tra
    FOREIGN KEY (QuizID) REFERENCES Quizzes(QuizID) ON DELETE CASCADE
);
GO

-- Table LessonItemProgress (depend on Users and LessonItems)
CREATE TABLE LessonItemProgress (
    ProgressID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    LessonItemID INT NOT NULL, -- video, quiz, material
    IsCompleted BIT DEFAULT 0,
    CompletionDate DATETIME NULL,
    LastAccessDate DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LessonItemProgress_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_LessonItemProgress_LessonItems FOREIGN KEY (LessonItemID) REFERENCES LessonItems(LessonItemID),
    CONSTRAINT UQ_LessonItemProgress_UserID_LessonItemID UNIQUE (UserID, LessonItemID)
);
GO

-- Table QuizAttempts (depend on Quizzes and Users)
CREATE TABLE QuizAttempts (
    AttemptID INT IDENTITY(1,1) PRIMARY KEY,
    QuizID INT NOT NULL,
    UserID INT NOT NULL,
    StartTime DATETIME NOT NULL DEFAULT GETDATE(),
    EndTime DATETIME NULL, -- NULL nếu chưa hoàn thành
    Score INT NULL, -- Điểm số (%)
    IsPassed BIT NULL, -- NULL nếu chưa hoàn thành
    FOREIGN KEY (QuizID) REFERENCES Quizzes(QuizID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Table Answers (depend on Questions)
CREATE TABLE Answers (
    AnswerID INT IDENTITY(1,1) PRIMARY KEY,
    QuestionID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    IsCorrect BIT NOT NULL DEFAULT 0, -- Đánh dấu đáp án đúng
    OrderIndex INT NOT NULL, -- Thứ tự đáp án
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID) ON DELETE CASCADE
);
GO

-- Table UserAnswers (depend on QuizAttempts, Questions and Answers)
CREATE TABLE UserAnswers (
    UserAnswerID INT IDENTITY(1,1) PRIMARY KEY,
    AttemptID INT NOT NULL,
    QuestionID INT NOT NULL,
    AnswerID INT NULL,
    IsCorrect BIT NULL, -- NULL nếu chưa chấm điểm
    FOREIGN KEY (AttemptID) REFERENCES QuizAttempts(AttemptID) ON DELETE CASCADE,
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID),
    FOREIGN KEY (AnswerID) REFERENCES Answers(AnswerID)
);
GO


-- 1. Insert Users
INSERT INTO Users (Username, Password, Email, Role, IsActive, FullName, Phone, Address, Avatar)
VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@example.com', 'admin', 1, 'Administrator', NULL, NULL, '/assets/imgs/avatars/admin.png'),
('instructor1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor1@example.com', 'instructor', 1, 'John Deep', '9876543210', '456 Teaching Ave, Education City', '/assets/imgs/avatars/instructor1.png'),
('instructor2', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor2@example.com', 'instructor', 1, 'Rose Bae', '0981972722', '123 Teaching Abc, NewYork City', '/assets/imgs/avatars/instructor2.png'),
('user', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'user@example.com', 'customer', 1, 'Test User', '1234567890', '123 Main St, City, Country', '/assets/imgs/avatars/default-user.png'),
('student1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'student1@example.com', 'customer', 1, 'Alice Student', '1111111111', '111 Student St', '/assets/imgs/avatars/student1.png'),
('student2', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'student2@example.com', 'customer', 1, 'Bob Learner', '2222222222', '222 Learning Ave', '/assets/imgs/avatars/student2.png'),
('student3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'student3@example.com', 'customer', 1, 'Charlie Scholar', '3333333333', '333 Scholar Blvd', '/assets/imgs/avatars/student3.png');

-- 2. Insert Categories
INSERT INTO Categories (Name, Description)
VALUES 
('Web Development', 'Courses related to web development technologies'),
('Data Science', 'Courses on data analysis, machine learning, and statistics'),
('Mobile Development', 'Courses for building mobile applications'),
('Game Development', 'Courses on game engines like Unity, Unreal, and game design'),
('JavaScript Programming', 'Courses about JavaScript, ES6+, and related frameworks');

-- 3. Insert Instructors
INSERT INTO Instructors (UserID, Biography, Specialization, ApprovalDate)
VALUES 
(2, 'Experienced software engineer with 10+ years teaching web development and programming languages.', 'Web Development', GETDATE()),
(3, 'Experienced software engineer with 5+ years teaching web designer.', 'Web Designer', GETDATE());

-- 4. Insert Courses
INSERT INTO Courses (Name, Description, Price, ImageUrl, Duration, Level, ApprovalStatus, SubmissionDate, ApprovalDate)
VALUES
('Complete Web Development Bootcamp', 'Learn HTML, CSS, JavaScript, React, Node.js and more to become a full-stack web developer', 1500000, 'assets/imgs/courses/Complete-Web-Development-Bootcamp.png', '12 weeks', 'Beginner', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Python for Data Science', 'Master Python for data analysis and visualization with pandas, numpy, and matplotlib', 1200000, 'assets/imgs/courses/Python-For-Data-Science.png', '8 weeks', 'Intermediate', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('React Native Mobile Apps', 'Build cross-platform mobile apps for iOS and Android using React Native', 1100050, 'assets/imgs/courses/React-Native-Mobile-Apps.png', '10 weeks', 'Intermediate', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Game Development with Unity', 'Learn to build interactive 2D and 3D games using Unity game engine and C#', 1700000, 'assets/imgs/courses/Game-Development-Unity.png', '6 weeks', 'Beginner', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('UI/UX Design Principles', 'Master the principles of user interface and user experience design', 1000050, 'assets/imgs/courses/UIUX-Design-Principles.png', '8 weeks', 'Beginner', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Advanced JavaScript', 'Deep dive into JavaScript concepts like closures, prototypes, and async programming', 1300000, 'assets/imgs/courses/Advanced-JavaScript.png', '10 weeks', 'Advanced', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Machine Learning Fundamentals', 'Introduction to core machine learning algorithms and techniques', 1400000, 'assets/imgs/courses/Machine-Learning-Fundamentals.png', '12 weeks', 'Intermediate', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('iOS App Development with Swift', 'Learn to build iOS applications using Swift and SwiftUI', 950000, 'assets/imgs/courses/iOS-App-Development-with-Swift.png', '10 weeks', 'Intermediate', 'pending', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE()));

-- 5. Insert Orders
INSERT INTO Orders (UserID, OrderDate, TotalAmount, Status, PaymentMethod)
VALUES 
(4, DATEADD(day, -10, GETDATE()), 2700000, 'completed', 'VNPAY'),
(4, DATEADD(day, -3, GETDATE()), 1100050, 'completed', 'VNPAY'),
(4, DATEADD(day, -20, GETDATE()), 1500000, 'completed', 'VNPAY'),
(5, DATEADD(day, -15, GETDATE()), 1500000, 'completed', 'VNPAY'),
(6, DATEADD(day, -10, GETDATE()), 2800000, 'completed', 'VNPAY');

-- 6. Insert CartItems
INSERT INTO CartItems (UserID, CourseID, Price, CreatedAt)
VALUES
(4, 1, 1500000, DATEADD(day, -1, GETDATE())),
(4, 2, 1200000, DATEADD(day, -1, GETDATE())),
(5, 2, 1200000, DATEADD(day, -2, GETDATE())),
(6, 3, 1100050, GETDATE());

-- 7. Insert CourseCategory
INSERT INTO CourseCategory (CourseID, CategoryID)
VALUES
(1, 1), -- Web Development Bootcamp -> Web Development
(2, 2), -- Python for Data Science -> Data Science
(3, 3), -- React Native -> Mobile Development
(4, 4), -- Game Development with Unity -> Game Development
(5, 1), -- UI/UX Design -> Should be related to Web Development
(6, 1), -- Advanced JavaScript -> Web Development
(7, 2), -- Machine Learning -> Data Science
(8, 3); -- iOS Development -> Mobile Development

-- 8. Insert CourseInstructors
INSERT INTO CourseInstructors (CourseID, InstructorID)
VALUES
(1, 1), -- Web Development - Instructor1
(2, 1), -- Python for Data Science - Instructor1
(3, 1), -- React Native - Instructor1
(4, 2), -- Business Fundamentals - Instructor2
(5, 2), -- UI/UX Design - Instructor2
(6, 1), -- Advanced JavaScript - Instructor1
(7, 1), -- Machine Learning - Instructor1
(8, 2); -- iOS Development - Instructor2

-- 9. Insert OrderDetails
INSERT INTO OrderDetails (OrderID, CourseID, Price)
VALUES 
(1, 1, 1500000),  -- User4's order1: Web Development Bootcamp
(1, 2, 1200000),  -- User4's order1: Python for Data Science
(2, 3, 1100050),  -- User4's order2: React Native Mobile Apps
(3, 1, 1500000),  -- User4's order3: Web Development Bootcamp
(4, 1, 1500000),  -- User5's order: Web Development Bootcamp
(5, 1, 1500000),  -- User6's order: Web Development Bootcamp
(5, 6, 1300000); -- User6's order: Advanced JavaScript

-- 10. Insert Lessons
INSERT INTO Lessons (CourseID, Title, OrderIndex)
VALUES 
-- Web Development Bootcamp Lessons
(1, 'Introduction to HTML', 1),
(1, 'Working with CSS', 2),
(1, 'JavaScript Fundamentals', 3),
(1, 'Building a Simple Website', 4),
-- Python for Data Science Lessons
(2, 'Python Basics', 1),
(2, 'Data Analysis with Pandas', 2),
(2, 'Data Visualization', 3),
-- React Native Lessons
(3, 'React Native Setup', 1),
(3, 'Building Your First App', 2),
-- Game Development Lessons
(4, 'Introduction to Unity', 1),
(4, 'Game Objects and Components', 2),
(4, 'C# Scripting for Games', 3),
-- Additional Game Development Lessons (for material/quiz references)
(4, 'Game Physics and Collisions', 4),
(4, 'Game UI Development', 5),
-- UI/UX Design Lessons
(5, 'Design Principles Fundamentals', 1),
-- Add lessons for other courses
(6, 'Advanced JavaScript Concepts', 1),
(7, 'Introduction to Machine Learning', 1),
(8, 'Getting Started with Swift', 1);

-- 11. Insert Ratings
INSERT INTO Ratings (CourseID, UserID, Stars, Comment)
VALUES
(1, 4, 5, N'Excellent course with hands-on projects!'),
(2, 4, 5, 'Excellent content! The instructor explains complex concepts in a simple way.'),
(1, 5, 4, 'Very good course. Covers all the basics and some advanced topics too.'),
(1, 6, 5, 'Best web development course I''ve taken. Highly recommended!'),
(3, 4, 4, N'Great Python course with practical examples.'),
(4, 4, 3, 'Good content but could use more exercises.'),
(3, 5, 5, N'Excellent React Native course. Very practical.'),
(4, 5, 4, 'Well-structured business course. Very informative.'),
(5, 4, 5, 'The UI/UX principles were clearly explained with great examples.'),
(6, 5, 5, 'Incredibly detailed JavaScript course. Learned a lot!'),
(6, 6, 4, 'Great advanced JavaScript course. Looking forward to applying these concepts.'),
(7, 6, 5, 'Excellent introduction to machine learning. Highly recommended!'),
(8, 5, 4, 'Good iOS development course. Clear explanations and examples.');

-- 12. Insert CourseProgress
INSERT INTO CourseProgress (UserID, CourseID, LastAccessDate, CompletionPercentage, IsCompleted)
VALUES
-- User 4's course progress (courses 1, 2, 3 from orders)
(4, 1, DATEADD(day, -5, GETDATE()), 15.0, 0),  -- Web Development
(4, 2, DATEADD(day, -2, GETDATE()), 5.0, 0),   -- Python for Data Science
(4, 3, GETDATE(), 25.0, 0),                    -- React Native

-- User 5's course progress (course 1 from order)
(5, 1, DATEADD(day, -1, GETDATE()), 45.0, 0),  -- Web Development

-- User 6's course progress (courses 1, 6 from order)
(6, 1, DATEADD(day, -5, GETDATE()), 20.0, 0),  -- Web Development
(6, 6, GETDATE(), 10.0, 0)                     -- Advanced JavaScript

-- 13. Insert RefundRequests
INSERT INTO RefundRequests (OrderID, UserID, RequestDate, Status, RefundAmount, Reason, ProcessedDate, AdminMessage, ProcessedBy)
VALUES
(1, 4, DATEADD(day, -8, GETDATE()), 'rejected', 89.99, 'Course content not as expected', DATEADD(day, -7, GETDATE()), 'Course content aligns with description', 1),
(3, 4, DATEADD(day, -3, GETDATE()), 'pending', 99.99, 'Found a better course for free', NULL, NULL, NULL);

-- 15. Insert Discussions
INSERT INTO Discussions (CourseID, LessonID, UserID, Content, IsResolved)
VALUES
(1, 1, 4, 'Can someone explain the difference between <div> and <span> tags?', 1),
(1, 3, 5, 'I don''t understand the difference between for...of and for...in loops', 0),
(2, 5, 6, 'I''m having trouble installing Python on Windows 11. Any suggestions?', 0),
(2, 6, 4, 'How do I efficiently filter rows in a DataFrame?', 1),
(3, 8, 4, 'Getting a bundler error when trying to run the app. Help needed!', 0),
(4, 10, 5, 'What''s the best approach for implementing player movement in Unity?', 1),
(4, 15, 6, 'How do I optimize my game for mobile devices?', 0),
(5, 11, 5, 'What color combinations work best for UI design?', 1),
(6, 12, 6, 'Can someone explain JavaScript closures with a simple example?', 0),
(7, 13, 5, 'My model has low accuracy. How can I improve it?', 0),
(8, 14, 4, 'When should I use SwiftUI instead of UIKit?', 1);

-- 16. Insert Videos
INSERT INTO Videos (LessonID, Title, Description, VideoUrl, Duration)
VALUES 
-- HTML Lesson Videos
(1, 'HTML Introduction', 'Basic HTML concepts and structure', 'assets/videos/introduce-advance-js.mp4', 135),
(1, 'HTML Tags and Elements', 'Working with different HTML elements', 'assets/videos/introduce-advance-js.mp4', 135),
-- CSS Lesson Videos
(2, 'CSS Basics', 'Introduction to CSS styling', 'assets/videos/introduce-advance-js.mp4', 135),
(2, 'CSS Layout', 'Understanding CSS layout techniques', 'assets/videos/introduce-advance-js.mp4', 135),
-- JavaScript Lesson Videos
(3, 'JavaScript Variables', 'Working with variables in JavaScript', 'assets/videos/introduce-advance-js.mp4', 135),
(3, 'JavaScript Functions', 'Understanding functions in JavaScript', 'assets/videos/introduce-advance-js.mp4', 135),
-- Add videos for other lessons
(5, 'Python Basics', 'Introduction to Python syntax', 'assets/videos/introduce-advance-js.mp4', 135),
(8, 'React Native Intro', 'Introduction to React Native', 'assets/videos/introduce-advance-js.mp4', 135),
(10, 'Unity Introduction', 'Introduction to Unity interface and workflow', 'assets/videos/unity-introduction.mp4', 180),
(15, 'Creating Game Objects', 'How to create and manipulate game objects in Unity', 'assets/videos/unity-gameobjects.mp4', 150),
(16, 'C# Scripting Basics', 'Introduction to C# scripting in Unity', 'assets/videos/unity-csharp-basics.mp4', 165),
(11, 'Design Principles', 'Introduction to design principles', 'assets/videos/introduce-advance-js.mp4', 135),
(12, 'Advanced JS Intro', 'Introduction to advanced JavaScript', 'assets/videos/introduce-advance-js.mp4', 135),
(13, 'Machine Learning Intro', 'Introduction to machine learning', 'assets/videos/introduce-advance-js.mp4', 135),
(14, 'Swift Intro', 'Introduction to Swift programming', 'assets/videos/introduce-advance-js.mp4', 135);

-- 17. Insert Materials
INSERT INTO Materials (LessonID, Title, Description, Content, FileUrl)
VALUES 
(1, 'HTML Reference Guide', 'Comprehensive HTML reference guide', 'This is a complete reference for HTML elements and attributes.', 'assets/materials/html-reference.pdf'),
(2, 'CSS Cheat Sheet', 'Quick reference for CSS properties', 'A cheat sheet containing all common CSS properties and values.', 'assets/materials/css-cheatsheet.pdf'),
(3, 'JavaScript Exercise Files', 'Practice exercises for JavaScript', 'A set of JavaScript exercises to practice your skills.', 'assets/materials/js-exercises.pdf'),
(5, 'Python Cheat Sheet', 'Quick reference for Python syntax', 'A complete reference for Python syntax and common functions.', 'assets/materials/python-cheatsheet.pdf'),
(8, 'React Native Setup Guide', 'Guide for setting up React Native', 'Complete guide to set up your React Native development environment.', 'assets/materials/react-native-setup.pdf'),
(10, 'Unity Installation Guide', 'Guide for setting up Unity', 'A comprehensive guide to install Unity and set up your game development environment.', 'assets/materials/unity-setup.pdf'),
(15, 'C# for Unity Reference', 'Quick reference for Unity C# scripting', 'Essential C# concepts and patterns specifically for Unity game development.', 'assets/materials/unity-csharp-reference.pdf'),
(16, 'Game Design Document Template', 'Template for planning your game', 'A structured template for planning and documenting your game concept and mechanics.', 'assets/materials/game-design-template.pdf'),
(11, 'Design Patterns Guide', 'Guide to common design patterns', 'A comprehensive guide to UI/UX design patterns.', 'assets/materials/design-patterns.pdf'),
(12, 'Advanced JS Reference', 'Reference for advanced JavaScript', 'Advanced JavaScript concepts and examples.', 'assets/materials/advanced-js-reference.pdf'),
(13, 'ML Algorithms Overview', 'Overview of ML algorithms', 'A guide to common machine learning algorithms.', 'assets/materials/ml-algorithms.pdf'),
(14, 'Swift Programming Guide', 'Guide to Swift programming', 'Comprehensive guide to Swift programming language.', 'assets/materials/swift-guide.pdf');

-- 18. Insert Quizzes
INSERT INTO Quizzes (LessonID, Title, Description, TimeLimit, PassingScore)
VALUES
(1, 'HTML Basics Quiz', 'Test your knowledge of HTML fundamentals', 20, 70),
(2, 'CSS Mastery Quiz', 'Check your understanding of CSS concepts', 25, 70),
(3, 'JavaScript Fundamentals Quiz', 'Test your JavaScript knowledge', 30, 75),
(4, 'Website Building Quiz', 'Final test for website building concepts', 45, 80),
(5, 'Python Syntax Quiz', 'Test your knowledge of Python syntax', 15, 70),
(6, 'Pandas Library Quiz', 'Check your understanding of Pandas', 30, 75),
(7, 'Data Visualization Quiz', 'Test your skills in data visualization', 25, 70),
(8, 'React Native Basics Quiz', 'Test your understanding of React Native setup', 20, 70),
(9, 'Mobile App Development Quiz', 'Final check on mobile app development concepts', 30, 75),
(10, 'Unity Basics Quiz', 'Test your understanding of Unity interface and components', 25, 70),
(15, 'Game Objects Quiz', 'Test your knowledge of game objects and prefabs', 20, 70),
(16, 'C# for Unity Quiz', 'Check your understanding of C# scripting in Unity', 30, 75),
(11, 'Design Principles Quiz', 'Test your knowledge of design principles', 20, 70),
(12, 'Advanced JS Quiz', 'Test your advanced JavaScript knowledge', 30, 75),
(13, 'Machine Learning Quiz', 'Test your understanding of ML concepts', 25, 70),
(14, 'Swift Programming Quiz', 'Test your Swift programming knowledge', 30, 70);

-- 19. Insert LessonProgress
INSERT INTO LessonProgress (UserID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
-- User 4's progress
(4, 1, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -15, GETDATE())), -- HTML completed
(4, 2, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -10, GETDATE())), -- CSS completed
(4, 3, 0, NULL, DATEADD(day, -5, GETDATE())),                          -- JS in progress
(4, 5, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -7, GETDATE())),   -- Python completed

-- User 5's progress
(5, 1, 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -20, GETDATE())),  -- HTML completed
(5, 2, 1, DATEADD(day, -18, GETDATE()), DATEADD(day, -18, GETDATE())),  -- CSS completed
(5, 3, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -15, GETDATE())),  -- JS completed
(5, 4, 0, NULL, DATEADD(day, -10, GETDATE())),                          -- Website building in progress
(5, 6, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -5, GETDATE())),    -- Pandas completed

-- User 6's progress
(6, 1, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -12, GETDATE())),  -- HTML completed
(6, 2, 0, NULL, DATEADD(day, -8, GETDATE())),                           -- CSS in progress
(6, 6, 1, DATEADD(day, -4, GETDATE()), DATEADD(day, -4, GETDATE())),    -- Pandas completed
(6, 7, 0, NULL, DATEADD(day, -3, GETDATE()));                           -- Data Viz in progress

-- 20. Insert PaymentTransactions
INSERT INTO PaymentTransactions (OrderID, TransactionType, Amount, Provider, ProviderTransactionID, Status, ResponseData)
VALUES
(1, 'payment', 189.98, 'Credit Card', 'TXN123456789', 'completed', '{"transaction_id": "TXN123456789", "status": "success"}'),
(2, 'payment', 79.99, 'PayPal', 'PP987654321', 'completed', '{"transaction_id": "PP987654321", "status": "success"}'),
(3, 'payment', 99.99, 'Credit Card', 'TXN234567890', 'completed', '{"transaction_id": "TXN234567890", "status": "success"}'),
(4, 'payment', 99.99, 'PayPal', 'PP876543210', 'completed', '{"transaction_id": "PP876543210", "status": "success"}'),
(5, 'payment', 209.98, 'VNPAY', 'VN345678901', 'completed', '{"transaction_id": "VN345678901", "status": "success"}');

-- 21. Insert DiscussionReplies
INSERT INTO DiscussionReplies (DiscussionID, UserID, Content, IsInstructorReply, IsAcceptedAnswer)
VALUES
(1, 2, '<div> is a block element while <span> is an inline element. Use <div> when you want a new line before and after the element, and <span> when you want to style text inline.', 1, 1),
(2, 2, 'for...in loops iterate over the enumerable properties of an object, while for...of loops iterate over the values of an iterable object like arrays.', 1, 0),
(1, 5, 'Also, <div> is commonly used for layout while <span> is used for styling small portions of text.', 0, 0),
(3, 2, 'Try downloading the latest installer from python.org and make sure to check "Add Python to PATH" during installation.', 1, 0),
(4, 5, 'Check if your Metro bundler is running. You might need to restart it with "npx react-native start".', 0, 0),
(5, 2, 'You can use df.loc[] for label-based filtering or df.query() for string expressions.', 1, 1),
(5, 6, 'Thanks! df.query() worked perfectly for my needs.', 0, 0),
(6, 3, 'For game UIs, keep controls intuitive and consistent. Use clear visual feedback for player actions and maintain a balanced HUD that doesn''t obstruct gameplay. For a good Unity game UI, consider using Canvas with Screen Space - Camera for 3D integration.', 1, 1),
(7, 3, 'Closures are functions that remember their lexical environment. Example: function outer() { let x = 10; function inner() { console.log(x); } return inner; }', 1, 0),
(8, 2, 'Try feature engineering, hyperparameter tuning, or using a more complex model. Also check if your data is imbalanced.', 1, 0),
(9, 2, 'Use SwiftUI for new apps targeting iOS 13+ for faster development. UIKit is better for complex UI, backward compatibility, or specific controls not available in SwiftUI.', 1, 1);

-- 22. Insert LessonItems
-- First, clear any existing data
DELETE FROM LessonItems;

-- Then insert data for Videos with a ROW_NUMBER to ensure unique OrderIndex
INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID)
SELECT 
    v.LessonID, 
    ROW_NUMBER() OVER (PARTITION BY v.LessonID ORDER BY v.VideoID), 
    'video', 
    v.VideoID
FROM Videos v;

-- Insert Materials with OrderIndex that continues from Videos
INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID)
SELECT 
    m.LessonID, 
    (SELECT COUNT(*) FROM LessonItems WHERE LessonID = m.LessonID) + 1,
    'material', 
    m.MaterialID
FROM Materials m;

-- Insert Quizzes with OrderIndex that continues from both Videos and Materials
INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID)
SELECT 
    q.LessonID, 
    (SELECT COUNT(*) FROM LessonItems WHERE LessonID = q.LessonID) + 1,
    'quiz', 
    q.QuizID
FROM Quizzes q;

-- 24. Insert Questions
INSERT INTO Questions (QuizID, Content, Type, Points, OrderIndex)
VALUES
(1, 'What does HTML stand for?', 'multiple_choice', 1, 1),
(1, 'Which tag is used to create a hyperlink?', 'multiple_choice', 1, 2),
(1, 'HTML is a programming language.', 'true_false', 1, 3),
(2, 'Which CSS property is used to change the text color?', 'multiple_choice', 1, 1),
(2, 'What does CSS stand for?', 'multiple_choice', 1, 2),
(3, 'What is the correct way to declare a JavaScript variable?', 'multiple_choice', 1, 1),
(3, 'JavaScript is case-sensitive.', 'true_false', 1, 2);

-- 25. Insert LessonItemProgress
INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 4, li.LessonItemID, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -15, GETDATE())
FROM LessonItems li
WHERE li.LessonID = 1 AND li.ItemType = 'video' AND li.OrderIndex = 1;

INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 4, li.LessonItemID, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -14, GETDATE())
FROM LessonItems li
WHERE li.LessonID = 1 AND li.ItemType = 'material' AND li.OrderIndex = 2;

INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 5, li.LessonItemID, 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -20, GETDATE())
FROM LessonItems li
WHERE li.LessonID IN (1, 2, 3);

INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 6, li.LessonItemID, 
       CASE WHEN li.ItemType = 'video' THEN 1 ELSE 0 END, 
       CASE WHEN li.ItemType = 'video' THEN DATEADD(day, -16, GETDATE()) ELSE NULL END, 
       DATEADD(day, -16, GETDATE())
FROM LessonItems li
WHERE li.LessonID = 1;

-- Additional progress data for new courses
INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 4, li.LessonItemID, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -7, GETDATE())
FROM LessonItems li
WHERE li.LessonID = 5 AND li.OrderIndex <= 2;

INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 5, li.LessonItemID, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -5, GETDATE())
FROM LessonItems li
WHERE li.LessonID = 6;

INSERT INTO LessonItemProgress (UserID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
SELECT 6, li.LessonItemID, 1, DATEADD(day, -3, GETDATE()), DATEADD(day, -3, GETDATE())
FROM LessonItems li
WHERE li.LessonID = 7 AND li.OrderIndex = 1;

-- 26. Insert QuizAttempts
INSERT INTO QuizAttempts (QuizID, UserID, StartTime, EndTime, Score, IsPassed)
VALUES
(1, 4, DATEADD(day, -14, GETDATE()), DATEADD(day, -14, GETDATE()), 80, 1),
(1, 5, DATEADD(day, -19, GETDATE()), DATEADD(day, -19, GETDATE()), 90, 1),
(2, 5, DATEADD(day, -17, GETDATE()), DATEADD(day, -17, GETDATE()), 85, 1),
(3, 5, DATEADD(day, -14, GETDATE()), DATEADD(day, -14, GETDATE()), 75, 1),
(2, 4, DATEADD(day, -10, GETDATE()), DATEADD(day, -10, GETDATE()), 70, 1),
(3, 4, DATEADD(day, -9, GETDATE()), DATEADD(day, -9, GETDATE()), 65, 0),
(4, 5, DATEADD(day, -7, GETDATE()), DATEADD(day, -7, GETDATE()), 88, 1),
(5, 6, DATEADD(day, -5, GETDATE()), DATEADD(day, -5, GETDATE()), 75, 1),
(6, 6, DATEADD(day, -4, GETDATE()), DATEADD(day, -4, GETDATE()), 92, 1),
(7, 6, DATEADD(day, -3, GETDATE()), DATEADD(day, -3, GETDATE()), 60, 0),
(8, 4, DATEADD(day, -2, GETDATE()), DATEADD(day, -2, GETDATE()), 78, 1);

-- 27. Insert Answers
INSERT INTO Answers (QuestionID, Content, IsCorrect, OrderIndex)
VALUES
(1, 'Hypertext Markup Language', 1, 1),
(1, 'High Text Machine Language', 0, 2),
(1, 'Hypertext Machine Language', 0, 3),
(1, 'High Text Markup Language', 0, 4),
(2, '<a>', 1, 1),
(2, '<link>', 0, 2),
(2, '<href>', 0, 3),
(2, '<hyperlink>', 0, 4),
(3, 'False', 1, 1), -- HTML is a markup language, not a programming language
(3, 'True', 0, 2),
(4, 'color', 1, 1),
(4, 'text-color', 0, 2),
(4, 'font-color', 0, 3),
(4, 'background-color', 0, 4),
(5, 'Cascading Style Sheets', 1, 1),
(5, 'Computer Style Sheets', 0, 2),
(5, 'Creative Style System', 0, 3),
(5, 'Colorful Style Sheets', 0, 4),
(6, 'var x = 5;', 1, 1),
(6, 'x = 5;', 0, 2),
(6, 'variable x = 5;', 0, 3),
(6, 'int x = 5;', 0, 4),
(7, 'True', 1, 1),
(7, 'False', 0, 2);

-- 28. Insert UserAnswers
INSERT INTO UserAnswers (AttemptID, QuestionID, AnswerID, IsCorrect)
VALUES
-- User 4's first quiz attempt - Quiz 1 (HTML Basics)
(1, 1, 1, 1), -- Question 1: HTML acronym - correct
(1, 2, 5, 1), -- Question 2: Hyperlink tag - correct
(1, 3, 3, 0), -- Question 3: HTML is programming language - incorrect (chose True)

-- User 5's first quiz attempt - Quiz 1 (HTML Basics)
(2, 1, 1, 1), -- Question 1: HTML acronym - correct
(2, 2, 5, 1), -- Question 2: Hyperlink tag - correct
(2, 3, 9, 1), -- Question 3: HTML is programming language - correct (chose False)

-- User 5's second quiz attempt - Quiz 2 (CSS Mastery)
(3, 4, 11, 1), -- Question 4: CSS text color property - correct
(3, 5, 15, 1), -- Question 5: CSS acronym - correct

-- User 5's third quiz attempt - Quiz 3 (JavaScript Fundamentals)
(4, 6, 19, 1), -- Question 6: JS variable declaration - correct
(4, 7, 23, 1), -- Question 7: JS case-sensitive - correct

-- User 4's second quiz attempt - Quiz 2 (CSS Mastery)
(5, 4, 11, 1), -- Question 4: CSS text color property - correct
(5, 5, 15, 1), -- Question 5: CSS acronym - correct

-- User 4's third quiz attempt - Quiz 3 (JavaScript Fundamentals)
(6, 6, 20, 0), -- Question 6: JS variable declaration - incorrect
(6, 7, 23, 1), -- Question 7: JS case-sensitive - correct

-- User 5's fourth quiz attempt - Quiz 4 (Website Building)
(7, 1, 1, 1), -- Using HTML questions for Website Building quiz
(7, 2, 5, 1),
(7, 3, 9, 1),

-- User 6's first quiz attempt - Quiz 5 (Python Syntax)
(8, 4, 11, 1), -- Using CSS questions for Python quiz
(8, 5, 15, 1),

-- User 6's second quiz attempt - Quiz 6 (Pandas Library)
(9, 6, 19, 1), -- Using JS questions for Pandas quiz
(9, 7, 23, 1),

-- User 6's third quiz attempt - Quiz 7 (Data Visualization)
(10, 6, 20, 0), -- Using JS questions for Data Visualization quiz
(10, 7, 24, 0),

-- User 4's fourth quiz attempt - Quiz 8 (React Native Basics)
(11, 4, 11, 1), -- Using CSS questions for React Native quiz
(11, 5, 15, 1);

GO

-- Create Index
CREATE INDEX IX_Users_Username ON Users(Username);
CREATE INDEX IX_Users_Email ON Users(Email);
CREATE INDEX IX_Orders_UserID ON Orders(UserID);
CREATE INDEX IX_Orders_OrderDate ON Orders(OrderDate);
CREATE INDEX IX_OrderDetails_OrderID ON OrderDetails(OrderID);
CREATE INDEX IX_OrderDetails_CourseID ON OrderDetails(CourseID);
CREATE INDEX IX_CourseCategory_CategoryID ON CourseCategory(CategoryID);
CREATE INDEX IX_CourseProgress_UserID ON CourseProgress(UserID);
CREATE INDEX IX_CourseProgress_CourseID ON CourseProgress(CourseID);
CREATE INDEX IX_RefundRequests_UserID ON RefundRequests(UserID);
CREATE INDEX IX_RefundRequests_Status ON RefundRequests(Status);
CREATE INDEX IX_Discussions_CourseID ON Discussions(CourseID);
CREATE INDEX IX_Discussions_LessonID ON Discussions(LessonID);
CREATE INDEX IX_Discussions_UserID ON Discussions(UserID);
CREATE INDEX IX_DiscussionReplies_DiscussionID ON DiscussionReplies(DiscussionID);
CREATE INDEX IX_DiscussionReplies_UserID ON DiscussionReplies(UserID);
CREATE INDEX IX_Quizzes_LessonID ON Quizzes(LessonID);
CREATE INDEX IX_Questions_QuizID ON Questions(QuizID);
CREATE INDEX IX_Answers_QuestionID ON Answers(QuestionID);
CREATE INDEX IX_QuizAttempts_QuizID ON QuizAttempts(QuizID);
CREATE INDEX IX_QuizAttempts_UserID ON QuizAttempts(UserID);
CREATE INDEX IX_UserAnswers_AttemptID ON UserAnswers(AttemptID);
CREATE INDEX IX_UserAnswers_QuestionID ON UserAnswers(QuestionID);
CREATE INDEX IX_PaymentTransactions_OrderID ON PaymentTransactions(OrderID);
CREATE INDEX IX_PaymentTransactions_RefundRequestID ON PaymentTransactions(RefundRequestID);
CREATE INDEX IX_PaymentTransactions_ProviderTransactionID ON PaymentTransactions(ProviderTransactionID);
CREATE INDEX IX_LessonItems_LessonID ON LessonItems(LessonID);
CREATE INDEX IX_LessonItems_ItemType_ItemID ON LessonItems(ItemType, ItemID);
CREATE INDEX IX_LessonItemProgress_UserID ON LessonItemProgress(UserID);
CREATE INDEX IX_LessonItemProgress_LessonItemID ON LessonItemProgress(LessonItemID);
GO

PRINT 'LightHouseCourse database has been created successfully with sample data.'
GO
