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

-- Table SuperUsers (for Admin and Instructors)
CREATE TABLE SuperUsers (
    SuperUserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    Role NVARCHAR(20) NOT NULL, -- 'admin' or 'instructor'
    IsActive BIT NOT NULL DEFAULT 1,
    FullName NVARCHAR(100),
    Phone NVARCHAR(20),
    Address NVARCHAR(255),
    Avatar NVARCHAR(255)
);
GO

-- Table Customers (for regular users/students)
CREATE TABLE Customers (
    CustomerID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
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
    SuperUserID INT NOT NULL UNIQUE,
    Biography NVARCHAR(1000),
    Specialization NVARCHAR(255),
    ApprovalDate DATETIME,
    Token NVARCHAR(255),
    TokenExpires DATETIME,
    FOREIGN KEY (SuperUserID) REFERENCES SuperUsers(SuperUserID) ON DELETE CASCADE
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
    CustomerID INT,
    OrderDate DATETIME NOT NULL,
    TotalAmount DECIMAL(10, 2) NOT NULL,
    Status NVARCHAR(20) NOT NULL, -- 'pending', 'completed', 'refunded'
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE SET NULL
);
GO

-- Table CartItems
CREATE TABLE CartItems (
    CartItemId INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    CourseID INT NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_CartItems_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    CONSTRAINT FK_CartItems_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    CONSTRAINT UQ_CartItems_CustomerID_CourseID UNIQUE (CustomerID, CourseID)
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

-- Table OrderDetails
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

-- Table Ratings (depend on Courses and Customers)
CREATE TABLE Ratings (
    RatingID INT PRIMARY KEY IDENTITY(1,1),
    CourseID INT NOT NULL,
    CustomerID INT NOT NULL,
    Stars INT NOT NULL CHECK (Stars BETWEEN 1 AND 5),
    Comment NVARCHAR(500),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Ratings_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    CONSTRAINT FK_Ratings_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    CONSTRAINT UQ_Ratings_Course_Customer UNIQUE (CourseID, CustomerID)
);
GO

-- Table CourseProgress (depend on Customers and Courses)
CREATE TABLE CourseProgress (
    ProgressID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    CourseID INT NOT NULL,
    LastAccessDate DATETIME DEFAULT GETDATE(),
    CompletionPercentage DECIMAL(5,2) DEFAULT 0, -- Percentage of course completed
    IsCompleted BIT DEFAULT 0,
    CONSTRAINT FK_CourseProgress_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    CONSTRAINT FK_CourseProgress_Courses FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
    CONSTRAINT UQ_CourseProgress_CustomerID_CourseID UNIQUE (CustomerID, CourseID)
);
GO

-- Table RefundRequests (depend on Orders and Customers)
CREATE TABLE RefundRequests (
    RefundID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NOT NULL,
    CustomerID INT NOT NULL,
    RequestDate DATETIME DEFAULT GETDATE(),
    Status NVARCHAR(20) NOT NULL DEFAULT 'pending', -- 'pending', 'approved', 'rejected'
    RefundAmount DECIMAL(10,2) NOT NULL,
    Reason NVARCHAR(500) NOT NULL,
    ProcessedDate DATETIME NULL,
    AdminMessage NVARCHAR(500) NULL,
    RefundPercentage INT NOT NULL DEFAULT 80, -- Default refund percentage
    ProcessedBy INT NULL,
    CONSTRAINT FK_RefundRequests_Orders FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    CONSTRAINT FK_RefundRequests_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    CONSTRAINT FK_RefundRequests_ProcessedBy FOREIGN KEY (ProcessedBy) REFERENCES SuperUsers(SuperUserID)
);
GO

-- Table Discussions (depend on Courses and Lessons)
CREATE TABLE Discussions (
    DiscussionID INT IDENTITY(1,1) PRIMARY KEY,
    CourseID INT NOT NULL,
    LessonID INT NULL,
    AuthorID INT NOT NULL,
    AuthorType NVARCHAR(20) NOT NULL, -- 'customer' or 'instructor'
    Content NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    IsResolved BIT DEFAULT 0,
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE NO ACTION
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
    LessonID INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX),
    TimeLimit INT NULL,
    PassingScore INT NOT NULL DEFAULT 70,
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- Table LessonProgress (depend on Customers and Lessons)
CREATE TABLE LessonProgress (
    ProgressID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    LessonID INT NOT NULL,
    IsCompleted BIT DEFAULT 0,
    CompletionDate DATETIME,
    LastAccessDate DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LessonProgress_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    CONSTRAINT FK_LessonProgress_Lessons FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID),
    CONSTRAINT UQ_LessonProgress_CustomerID_LessonID UNIQUE (CustomerID, LessonID)
);
GO

-- Table PaymentTransactions (depend on Orders and RefundRequests)
CREATE TABLE PaymentTransactions (
    TransactionID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NULL,
    RefundRequestID INT NULL,
    TransactionType NVARCHAR(20) NOT NULL,
    Provider NVARCHAR(20) NOT NULL,
    ProviderTransactionID NVARCHAR(100) NOT NULL,
    BankAccountInfo NVARCHAR(255) NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (RefundRequestID) REFERENCES RefundRequests(RefundID)
);
GO

-- Table DiscussionReplies (depend on Discussions)
CREATE TABLE DiscussionReplies (
    ReplyID INT IDENTITY(1,1) PRIMARY KEY,
    DiscussionID INT NOT NULL,
    AuthorID INT NOT NULL,
    AuthorType NVARCHAR(20) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (DiscussionID) REFERENCES Discussions(DiscussionID) ON DELETE CASCADE
);
GO

-- Table LessonItems (depend on Lessons)
CREATE TABLE LessonItems (
    LessonItemID INT IDENTITY(1,1) PRIMARY KEY,
    LessonID INT NOT NULL,
    OrderIndex INT NOT NULL,
    ItemType NVARCHAR(10) NOT NULL,
    ItemID INT NOT NULL,
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE,
    CONSTRAINT UQ_LessonItems_Lesson_Order UNIQUE (LessonID, OrderIndex),
    CONSTRAINT UQ_LessonItems_Item UNIQUE (ItemType, ItemID)
);
GO

-- Table Questions (depend on Quizzes)
CREATE TABLE Questions (
    QuestionID INT IDENTITY(1,1) PRIMARY KEY,
    QuizID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(20) NOT NULL,
    Points INT NOT NULL DEFAULT 1,
    OrderIndex INT NOT NULL,
    FOREIGN KEY (QuizID) REFERENCES Quizzes(QuizID) ON DELETE CASCADE
);
GO

-- Table LessonItemProgress (depend on Customers and LessonItems)
CREATE TABLE LessonItemProgress (
    ProgressID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    LessonItemID INT NOT NULL,
    IsCompleted BIT DEFAULT 0,
    CompletionDate DATETIME NULL,
    LastAccessDate DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LessonItemProgress_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    CONSTRAINT FK_LessonItemProgress_LessonItems FOREIGN KEY (LessonItemID) REFERENCES LessonItems(LessonItemID),
    CONSTRAINT UQ_LessonItemProgress_CustomerID_LessonItemID UNIQUE (CustomerID, LessonItemID)
);
GO

-- Table QuizAttempts (depend on Quizzes and Customers)
CREATE TABLE QuizAttempts (
    AttemptID INT IDENTITY(1,1) PRIMARY KEY,
    QuizID INT NOT NULL,
    CustomerID INT NOT NULL,
    StartTime DATETIME NOT NULL DEFAULT GETDATE(),
    EndTime DATETIME NULL,
    Score INT NULL,
    IsPassed BIT NULL,
    FOREIGN KEY (QuizID) REFERENCES Quizzes(QuizID) ON DELETE CASCADE,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);
GO

-- Table Answers (depend on Questions)
CREATE TABLE Answers (
    AnswerID INT IDENTITY(1,1) PRIMARY KEY,
    QuestionID INT NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    IsCorrect BIT NOT NULL DEFAULT 0,
    OrderIndex INT NOT NULL,
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID) ON DELETE CASCADE
);
GO

-- Table UserAnswers (depend on QuizAttempts, Questions and Answers)
CREATE TABLE UserAnswers (
    UserAnswerID INT IDENTITY(1,1) PRIMARY KEY,
    AttemptID INT NOT NULL,
    QuestionID INT NOT NULL,
    AnswerID INT NULL,
    IsCorrect BIT NULL,
    FOREIGN KEY (AttemptID) REFERENCES QuizAttempts(AttemptID) ON DELETE CASCADE,
    FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID),
    FOREIGN KEY (AnswerID) REFERENCES Answers(AnswerID)
);
GO

-- Insert SuperUsers
INSERT INTO SuperUsers (Username, Password, Email, Role, IsActive, FullName, Phone, Address, Avatar)
VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@example.com', 'admin', 1, 'Administrator', '0778167802', '123 Admin House', '/assets/imgs/avatars/default-admin.png'),
('admin2', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin2@example.com', 'admin', 1, 'Jane Admin', '0901234567', '456 Admin Street', '/assets/imgs/avatars/default-admin.png'),
('admin3', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin3@example.com', 'admin', 1, 'Mark Supervisor', '0901234568', '789 Admin Avenue', '/assets/imgs/avatars/default-admin.png'),
('instructor1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor1@example.com', 'instructor', 1, 'John Deep', '0912345678', '123 Teaching St', '/assets/imgs/avatars/instructor1.png'),
('instructor2', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor2@example.com', 'instructor', 1, 'Rose Bae', '0923456789', '456 Education Blvd', '/assets/imgs/avatars/instructor2.png'),
('instructor3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor3@example.com', 'instructor', 1, 'Michael Smith', '0934567890', '789 Learning Lane', '/assets/imgs/avatars/default-instructor.png'),
('instructor4', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor4@example.com', 'instructor', 1, 'David Johnson', '0945678901', '101 Knowledge Way', '/assets/imgs/avatars/default-instructor.png'),
('instructor5', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor5@example.com', 'instructor', 1, 'Sarah Williams', '0956789012', '202 Teacher Road', '/assets/imgs/avatars/default-instructor.png'),
('instructor6', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor6@example.com', 'instructor', 1, 'Emily Brown', '0967890123', '303 Professor Path', '/assets/imgs/avatars/default-instructor.png'),
('instructor7', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor7@example.com', 'instructor', 1, 'Robert Davis', '0978901234', '404 Instructor Drive', '/assets/imgs/avatars/default-instructor.png'),
('instructor8', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'instructor8@example.com', 'instructor', 1, 'Jennifer Wilson', '0989012345', '505 Faculty Circle', '/assets/imgs/avatars/default-instructor.png');

-- Insert Customers
INSERT INTO Customers (Username, Password, Email, IsActive, FullName, Phone, Address, Avatar)
VALUES
('user', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'user@example.com', 1, 'Test User', '1234567890', '123 Main St, City, Country', '/assets/imgs/avatars/default-user.png'),
('student1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'student1@example.com', 1, 'Alice Student', '1111111111', '111 Student St', '/assets/imgs/avatars/student1.png'),
('student2', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'student2@example.com', 1, 'Bob Learner', '2222222222', '222 Learning Ave', '/assets/imgs/avatars/student2.png'),
('student3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'student3@example.com', 1, 'Charlie Scholar', '3333333333', '333 Scholar Blvd', '/assets/imgs/avatars/student3.png'),
('john_doe', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'john.doe@example.com', 1, 'John Doe', '0901111111', '123 Elm Street', '/assets/imgs/avatars/default-user.png'),
('jane_doe', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'jane.doe@example.com', 1, 'Jane Doe', '0902222222', '456 Oak Avenue', '/assets/imgs/avatars/default-user.png'),
('mike_smith', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'mike.smith@example.com', 1, 'Mike Smith', '0903333333', '789 Pine Boulevard', '/assets/imgs/avatars/default-user.png'),
('sara_jones', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'sara.jones@example.com', 1, 'Sara Jones', '0904444444', '101 Maple Drive', '/assets/imgs/avatars/default-user.png'),
('david_brown', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'david.brown@example.com', 1, 'David Brown', '0905555555', '202 Cedar Lane', '/assets/imgs/avatars/default-user.png'),
('lisa_white', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'lisa.white@example.com', 1, 'Lisa White', '0906666666', '303 Birch Road', '/assets/imgs/avatars/default-user.png'),
('james_green', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'james.green@example.com', 1, 'James Green', '0907777777', '404 Walnut Court', '/assets/imgs/avatars/default-user.png'),
('emma_black', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'emma.black@example.com', 1, 'Emma Black', '0908888888', '505 Cherry Circle', '/assets/imgs/avatars/default-user.png'),
('alex_taylor', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'alex.taylor@example.com', 1, 'Alex Taylor', '0909999999', '606 Spruce Place', '/assets/imgs/avatars/default-user.png'),
('olivia_lee', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'olivia.lee@example.com', 1, 'Olivia Lee', '0900000000', '707 Redwood Street', '/assets/imgs/avatars/default-user.png');

-- Insert Instructors
INSERT INTO Instructors (SuperUserID, Biography, Specialization, ApprovalDate)
VALUES 
(4, 'Experienced software engineer with 10+ years teaching web development and programming languages.', 'Web Development', DATEADD(month, -6, GETDATE())),
(5, 'Experienced software engineer with 5+ years teaching web designer.', 'Web Designer', DATEADD(month, -5, GETDATE())),
(6, 'Former senior developer at Google with expertise in AI and machine learning.', 'Machine Learning', DATEADD(month, -4, GETDATE())),
(7, 'Mobile development expert with 8 years of industry experience.', 'Mobile Development', DATEADD(month, -7, GETDATE())),
(8, 'Data scientist with background in statistical analysis and big data.', 'Data Science', DATEADD(month, -3, GETDATE())),
(9, 'UX/UI specialist who has worked with major brands on user experience optimization.', 'UX/UI Design', DATEADD(month, -8, GETDATE())),
(10, 'Game development guru with experience at major gaming studios.', 'Game Development', DATEADD(month, -9, GETDATE())),
(11, 'Frontend development expert specializing in modern JavaScript frameworks.', 'Frontend Development', DATEADD(month, -2, GETDATE()));

-- Insert Categories
INSERT INTO Categories (Name, Description)
VALUES 
('Web Development', 'Courses related to web development technologies'),
('Data Science', 'Courses on data analysis, machine learning, and statistics'),
('Mobile Development', 'Courses for building mobile applications'),
('Game Development', 'Courses on game engines like Unity, Unreal, and game design'),
('JavaScript Programming', 'Courses about JavaScript, ES6+, and related frameworks'),
('UI/UX Design', 'Courses focused on user interface and user experience design'),
('Databases', 'Courses about database design, SQL, NoSQL and optimization'),
('DevOps', 'Courses on deployment, CI/CD, and cloud infrastructure'),
('Cybersecurity', 'Courses on network security, ethical hacking, and security practices'),
('Artificial Intelligence', 'Courses on AI concepts, neural networks, and deep learning'),
('Blockchain', 'Courses on blockchain technology, cryptocurrency, and smart contracts'),
('IoT Development', 'Courses on Internet of Things and embedded systems'),
('Python Programming', 'Courses specifically focused on Python language and libraries'),
('Frontend Development', 'Courses on HTML, CSS, JavaScript and modern frameworks'),
('Backend Development', 'Courses on server-side programming and API development');

-- Insert Courses
INSERT INTO Courses (Name, Description, Price, ImageUrl, Duration, Level, ApprovalStatus, SubmissionDate, ApprovalDate)
VALUES
('Complete Web Development Bootcamp', 'Learn HTML, CSS, JavaScript, React, Node.js and more to become a full-stack web developer', 1500000, 'assets/imgs/courses/Complete-Web-Development-Bootcamp.png', '12 weeks', 'Beginner', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Python for Data Science', 'Master Python for data analysis and visualization with pandas, numpy, and matplotlib', 1200000, 'assets/imgs/courses/Python-For-Data-Science.png', '8 weeks', 'Intermediate', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('React Native Mobile Apps', 'Build cross-platform mobile apps for iOS and Android using React Native', 1100050, 'assets/imgs/courses/React-Native-Mobile-Apps.png', '10 weeks', 'Intermediate', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Game Development with Unity', 'Learn to build interactive 2D and 3D games using Unity game engine and C#', 1700000, 'assets/imgs/courses/Game-Development-Unity.png', '6 weeks', 'Beginner', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('UI/UX Design Principles', 'Master the principles of user interface and user experience design', 1000050, 'assets/imgs/courses/UIUX-Design-Principles.png', '8 weeks', 'Beginner', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Advanced JavaScript', 'Deep dive into JavaScript concepts like closures, prototypes, and async programming', 1300000, 'assets/imgs/courses/Advanced-JavaScript.png', '10 weeks', 'Advanced', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('Machine Learning Fundamentals', 'Introduction to core machine learning algorithms and techniques', 1400000, 'assets/imgs/courses/Machine-Learning-Fundamentals.png', '12 weeks', 'Intermediate', 'approved', DATEADD(month, -5, GETDATE()), DATEADD(month, -5, GETDATE())),
('iOS App Development with Swift', 'Learn to build iOS applications using Swift and SwiftUI', 950000, 'assets/imgs/courses/iOS-App-Development-with-Swift.png', '10 weeks', 'Intermediate', 'approved', DATEADD(month, -1, GETDATE()), DATEADD(month, -1, GETDATE())),
('SQL Database Mastery', 'Master SQL for database management, queries, and optimization', 890000, 'assets/imgs/courses/database-mastery.png', '6 weeks', 'Beginner', 'approved', DATEADD(month, -3, GETDATE()), DATEADD(month, -3, GETDATE())),
('DevOps Engineering', 'Learn CI/CD, Docker, Kubernetes and cloud deployment', 1600000, 'assets/imgs/courses/devops.png', '10 weeks', 'Advanced', 'approved', DATEADD(month, -4, GETDATE()), DATEADD(month, -4, GETDATE())),
('Ethical Hacking', 'Learn penetration testing and cyber security fundamentals', 1750000, 'assets/imgs/courses/ethical-hacking.png', '8 weeks', 'Intermediate', 'approved', DATEADD(month, -2, GETDATE()), DATEADD(month, -2, GETDATE())),
('Deep Learning with TensorFlow', 'Master deep neural networks using TensorFlow and Keras', 1500000, 'assets/imgs/courses/deep-learning.png', '12 weeks', 'Advanced', 'approved', DATEADD(month, -6, GETDATE()), DATEADD(month, -6, GETDATE())),
('Blockchain Development', 'Build decentralized applications with Ethereum and Solidity', 1800000, 'assets/imgs/courses/blockchain.png', '14 weeks', 'Advanced', 'pending', DATEADD(day, -15, GETDATE()), NULL),
('Internet of Things Fundamentals', 'Learn to build connected devices using Arduino and Raspberry Pi', 1250000, 'assets/imgs/courses/iot.png', '8 weeks', 'Intermediate', 'pending', DATEADD(day, -20, GETDATE()), NULL),
('Vue.js for Frontend Development', 'Master modern UI development with Vue.js', 980000, 'assets/imgs/courses/vuejs.png', '6 weeks', 'Intermediate', 'rejected', DATEADD(month, -1, GETDATE()), DATEADD(day, -25, GETDATE())),
('Django Web Framework', 'Build robust web applications with Python and Django', 1100000, 'assets/imgs/courses/django.png', '8 weeks', 'Intermediate', 'pending', DATEADD(day, -10, GETDATE()), NULL);

-- Insert CourseCategory
INSERT INTO CourseCategory (CourseID, CategoryID)
VALUES
(1, 1), (1, 14), (1, 15), (2, 2), (2, 13), (3, 3), (4, 4), (5, 6), (5, 14), (6, 1), (6, 5), (6, 14),
(7, 2), (7, 10), (8, 3), (9, 7), (10, 8), (11, 9), (12, 10), (12, 2), (13, 11), (14, 12), (15, 14), (15, 5), (16, 15), (16, 13);

-- Insert CourseInstructors
INSERT INTO CourseInstructors (CourseID, InstructorID)
VALUES
(1, 1), (2, 5), (3, 4), (4, 7), (5, 6), (6, 1), (6, 8), (7, 3), (7, 5), (8, 4), (9, 2), (9, 5), (10, 7), (11, 3), (12, 3), (12, 5), (13, 8), (14, 7), (15, 1), (15, 8), (16, 1), (16, 5);

-- Insert Orders
INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, Status)
VALUES 
(1, DATEADD(day, -60, GETDATE()), 2700000, 'completed'),
(1, DATEADD(day, -45, GETDATE()), 1100050, 'completed'),
(2, DATEADD(day, -55, GETDATE()), 1500000, 'completed'),
(3, DATEADD(day, -50, GETDATE()), 2800000, 'completed'),
(4, DATEADD(day, -40, GETDATE()), 1700000, 'completed'),
(5, DATEADD(day, -35, GETDATE()), 2500050, 'completed'),
(6, DATEADD(day, -30, GETDATE()), 1300000, 'completed'),
(7, DATEADD(day, -25, GETDATE()), 1400000, 'completed'),
(8, DATEADD(day, -20, GETDATE()), 1750000, 'completed'),
(9, DATEADD(day, -15, GETDATE()), 2290000, 'completed'),
(10, DATEADD(day, -10, GETDATE()), 1500000, 'completed'),
(11, DATEADD(day, -5, GETDATE()), 1400000, 'completed'),
(12, DATEADD(day, -4, GETDATE()), 1000050, 'pending'),
(13, DATEADD(day, -3, GETDATE()), 890000, 'pending'),
(14, DATEADD(day, -2, GETDATE()), 2900000, 'pending');

-- Insert OrderDetails
INSERT INTO OrderDetails (OrderID, CourseID, Price)
VALUES 
(1, 1, 1500000), (1, 2, 1200000), (2, 3, 1100050), (3, 1, 1500000), (4, 1, 1500000), 
(4, 6, 1300000), (5, 4, 1700000), (6, 5, 1000050), (6, 10, 1500000), (7, 6, 1300000), (8, 7, 1400000), 
(9, 11, 1750000), (10, 9, 890000), (10, 12, 1400000), (11, 1, 1500000), (12, 7, 1400000), (13, 5, 1000050), 
(14, 9, 890000), (15, 6, 1300000), (15, 10, 1600000);

-- Insert CartItems
INSERT INTO CartItems (CustomerID, CourseID, Price, CreatedAt)
VALUES
(1, 12, 1500000, DATEADD(day, -1, GETDATE())), (2, 9, 890000, DATEADD(day, -1, GETDATE())), 
(3, 7, 1400000, DATEADD(day, -2, GETDATE())), (4, 6, 1300000, GETDATE()), (5, 11, 1750000, GETDATE()), 
(6, 10, 1600000, DATEADD(day, -3, GETDATE())), (7, 8, 950000, DATEADD(day, -1, GETDATE())), 
(8, 12, 1500000, DATEADD(day, -2, GETDATE())), (9, 13, 1800000, GETDATE()), 
(10, 14, 1250000, DATEADD(day, -4, GETDATE()));

-- Insert Lessons
INSERT INTO Lessons (CourseID, Title, OrderIndex)
VALUES 
(1, 'Introduction to HTML', 1), (1, 'Working with CSS', 2), (1, 'JavaScript Fundamentals', 3), 
(1, 'Building a Simple Website', 4), (1, 'Responsive Design', 5), (1, 'Introduction to React', 6), 
(1, 'Building a React App', 7), (1, 'Node.js Basics', 8), (1, 'Building a RESTful API', 9), 
(1, 'Database Integration', 10), (1, 'Deployment and DevOps', 11), (1, 'Final Project', 12),
(2, 'Python Basics', 1), (2, 'Data Analysis with Pandas', 2), (2, 'Data Visualization', 3), 
(2, 'Statistical Analysis', 4), (2, 'Machine Learning with Scikit-learn', 5), 
(2, 'Data Cleaning and Preprocessing', 6), (2, 'Working with APIs', 7), (2, 'Big Data Processing', 8),
(3, 'React Native Setup', 1), (3, 'Building Your First App', 2), (3, 'Navigation and Routing', 3), 
(3, 'Managing State', 4), (3, 'Using Native Components', 5), (3, 'Handling User Input', 6), 
(3, 'Networking and APIs', 7), (3, 'Deployment to App Stores', 8), (3, 'Performance Optimization', 9), 
(3, 'Final Project', 10),
(4, 'Introduction to Unity', 1), (4, 'Game Objects and Components', 2), (4, 'C# Scripting for Games', 3), 
(4, 'Game Physics and Collisions', 4), (4, 'Game UI Development', 5), (4, 'Audio in Games', 6), 
(4, 'Character Animation', 7), (4, 'Game AI Basics', 8), (4, 'Optimizing Game Performance', 9), 
(4, 'Publishing Your Game', 10),
(5, 'Design Principles Fundamentals', 1), (5, 'User Research Methods', 2), (5, 'Wireframing and Prototyping', 3), 
(5, 'Color Theory for UI', 4), (5, 'Typography in Design', 5), (5, 'Creating User Personas', 6), 
(5, 'Usability Testing', 7), (5, 'Mobile-First Design', 8),
(6, 'Advanced JavaScript Concepts', 1), (6, 'Closures and Scope', 2), (6, 'Prototypes and Inheritance', 3), 
(6, 'Asynchronous JavaScript', 4), (6, 'ES6+ Features', 5), (6, 'Functional Programming', 6), 
(6, 'Design Patterns', 7), (6, 'Testing and Debugging', 8), (6, 'Performance Optimization', 9), 
(6, 'Advanced Project', 10),
(7, 'Introduction to Machine Learning', 1), (7, 'Supervised Learning Algorithms', 2), 
(7, 'Unsupervised Learning Algorithms', 3), (7, 'Feature Engineering', 4), (7, 'Model Evaluation', 5), 
(7, 'Neural Networks Basics', 6), (7, 'Deep Learning Introduction', 7), (7, 'Practical ML Applications', 8), 
(7, 'Ethical Considerations in AI', 9), (7, 'Final ML Project', 10),
(8, 'Swift Programming Fundamentals', 1), (8, 'iOS App Architecture', 2), (8, 'UIKit Essentials', 3), 
(8, 'SwiftUI Introduction', 4), (8, 'Working with Data', 5), (8, 'Networking and APIs in iOS', 6), 
(8, 'Core Data and Persistence', 7), (8, 'iOS App Testing', 8), (8, 'App Store Deployment', 9), 
(8, 'Final iOS Project', 10),
(9, 'Introduction to Databases', 1), (9, 'SQL Basics: SELECT, INSERT, UPDATE, DELETE', 2), 
(9, 'Advanced Queries and Joins', 3), (9, 'Database Design and Normalization', 4), 
(9, 'Indexing and Performance', 5), (9, 'Stored Procedures and Functions', 6), 
(9, 'Transactions and Concurrency', 7), (9, 'Database Security', 8), (9, 'Real-world Database Projects', 9),
(10, 'Introduction to DevOps', 1), (10, 'Version Control with Git', 2), (10, 'Continuous Integration', 3), 
(10, 'Continuous Deployment', 4), (10, 'Containerization with Docker', 5), 
(10, 'Container Orchestration with Kubernetes', 6), (10, 'Infrastructure as Code', 7), 
(10, 'Monitoring and Logging', 8), (10, 'Cloud Services Integration', 9), (10, 'DevOps Best Practices', 10),
(11, 'Introduction to Ethical Hacking', 1), (11, 'Reconnaissance Techniques', 2), (11, 'Scanning Networks', 3), 
(11, 'Enumeration', 4), (11, 'Vulnerability Analysis', 5), (11, 'System Hacking', 6), 
(11, 'Malware Threats', 7), (11, 'Sniffing', 8), (11, 'Social Engineering', 9), (11, 'Denial of Service', 10), 
(11, 'Session Hijacking', 11), (11, 'Web Server and Application Security', 12),
(12, 'Introduction to Deep Learning', 1), (12, 'TensorFlow Basics', 2), (12, 'Building Neural Networks', 3), 
(12, 'Convolutional Neural Networks', 4), (12, 'Recurrent Neural Networks', 5), (12, 'Transfer Learning', 6), 
(12, 'Generative Models', 7), (12, 'Natural Language Processing', 8), (12, 'Computer Vision Applications', 9), 
(12, 'Deploying Deep Learning Models', 10),
(13, 'Blockchain Fundamentals', 1), (13, 'Cryptography Basics', 2), (13, 'Bitcoin Protocol', 3), 
(13, 'Ethereum and Smart Contracts', 4), (13, 'Solidity Programming', 5), (13, 'Decentralized Applications (DApps)', 6), 
(13, 'Web3.js and Frontend Integration', 7), (13, 'Testing Smart Contracts', 8), (13, 'Security Best Practices', 9), 
(13, 'Real-world Blockchain Project', 10),
(14, 'Introduction to IoT', 1), (14, 'Sensors and Actuators', 2), (14, 'Microcontrollers and Arduino', 3), 
(14, 'Raspberry Pi for IoT', 4), (14, 'Networking Protocols for IoT', 5), (14, 'IoT Cloud Platforms', 6), 
(14, 'IoT Security', 7), (14, 'IoT Data Analytics', 8), (14, 'Building End-to-End IoT Solutions', 9), 
(14, 'Future of IoT and Industry Applications', 10),
(15, 'Introduction to Vue.js', 1), (15, 'Vue Components', 2), (15, 'Vue Directives and Event Handling', 3), 
(15, 'Vue Reactivity System', 4), (15, 'Vue Router', 5), (15, 'State Management with Vuex', 6), 
(15, 'Forms and Validation', 7), (15, 'Vue.js Best Practices', 8), (15, 'Testing Vue Applications', 9), 
(15, 'Building a Production-ready Vue.js App', 10),
(16, 'Introduction to Django', 1), (16, 'Django Models and Databases', 2), (16, 'Django Views and Templates', 3), 
(16, 'Django Forms', 4), (16, 'Django Admin Interface', 5), (16, 'Authentication and Permissions', 6), 
(16, 'REST APIs with Django Rest Framework', 7), (16, 'Testing Django Applications', 8), 
(16, 'Django Deployment', 9), (16, 'Building a Complete Django Project', 10);

-- Insert Ratings
INSERT INTO Ratings (CourseID, CustomerID, Stars, Comment)
VALUES
(1, 1, 5, N'Excellent course with hands-on projects!'),
(2, 1, 5, 'Excellent content! The instructor explains complex concepts in a simple way.'),
(1, 2, 4, 'Very good course. Covers all the basics and some advanced topics too.'),
(1, 3, 5, 'Best web development course I''ve taken. Highly recommended!'),
(3, 1, 4, N'Great React Native course with practical examples.'),
(4, 1, 3, 'Good content but could use more exercises.'),
(3, 2, 5, N'Excellent React Native course. Very practical.'),
(4, 2, 4, 'Well-structured Unity course. Very informative.'),
(5, 1, 5, 'The UI/UX principles were clearly explained with great examples.'),
(6, 2, 5, 'Incredibly detailed JavaScript course. Learned a lot!'),
(6, 3, 4, 'Great advanced JavaScript course. Looking forward to applying these concepts.'),
(7, 3, 5, 'Excellent introduction to machine learning. Highly recommended!'),
(8, 2, 4, 'Good iOS development course. Clear explanations and examples.'),
(9, 4, 5, 'Best SQL course I''ve taken. Very comprehensive.'),
(10, 5, 4, 'Solid DevOps course with practical examples.'),
(11, 6, 5, 'Excellent cybersecurity content. Very relevant examples.'),
(12, 7, 3, 'Good content but too theoretical at times.'),
(13, 8, 4, 'Interesting blockchain course. Would have liked more hands-on projects.'),
(14, 9, 5, 'Fantastic IoT course with great practical exercises.'),
(15, 10, 2, 'Outdated content. Needs to be refreshed with newer Vue.js versions.'),
(16, 11, 5, 'Excellent Django course. Very thorough and practical.'),
(1, 4, 4, 'Good introduction to web development. Helpful for beginners.'),
(2, 5, 5, 'Great Python course. The data science applications were very useful.'),
(3, 6, 3, 'Decent React Native course but lacks advanced topics.'),
(4, 7, 5, 'Excellent Unity course with practical projects.');


-- Insert RefundRequests
INSERT INTO RefundRequests (OrderID, CustomerID, RequestDate, Status, RefundAmount, Reason, ProcessedDate, AdminMessage, ProcessedBy, RefundPercentage)
VALUES
(1, 1, DATEADD(day, -55, GETDATE()), 'rejected', 1200000, 'Course content not as expected', DATEADD(day, -54, GETDATE()), 'Course content aligns with description', 1, 80),
(3, 1, DATEADD(day, -25, GETDATE()), 'approved', 1200000, 'Found a better course for free', DATEADD(day, -23, GETDATE()), 'Approved as per policy', 1, 80),
(5, 3, DATEADD(day, -45, GETDATE()), 'rejected', 1500000, 'Too difficult for my level', DATEADD(day, -44, GETDATE()), 'Course difficulty is clearly stated in description', 2, 80),
(6, 4, DATEADD(day, -38, GETDATE()), 'approved', 1360000, 'Technical issues with course videos', DATEADD(day, -36, GETDATE()), 'Refund approved due to technical issues', 1, 80),
(8, 6, DATEADD(day, -28, GETDATE()), 'approved', 1040000, 'Content is outdated', DATEADD(day, -25, GETDATE()), 'Approved - content will be updated soon', 3, 80),
(9, 7, DATEADD(day, -22, GETDATE()), 'rejected', 1400000, 'Found similar content for free', DATEADD(day, -20, GETDATE()), 'Free content is not as comprehensive', 2, 80),
(11, 9, DATEADD(day, -10, GETDATE()), 'pending', 712000, 'Course not as advanced as described', NULL, NULL, NULL, 80),
(12, 10, DATEADD(day, -8, GETDATE()), 'pending', 1200000, 'Not what I was looking for', NULL, NULL, NULL, 80),
(13, 12, DATEADD(day, -3, GETDATE()), 'pending', 1000050, 'Changed my mind', NULL, NULL, NULL, 80),
(15, 14, DATEADD(day, -1, GETDATE()), 'pending', 1600000, 'Financial reasons', NULL, NULL, NULL, 80);

-- Insert Discussions
INSERT INTO Discussions (CourseID, LessonID, AuthorID, AuthorType, Content, IsResolved)
VALUES
(1, 1, 1, 'customer', 'Can someone explain the difference between <div> and <span> tags?', 1),
(1, 3, 2, 'customer', 'I don''t understand the difference between for...of and for...in loops', 0),
(2, 13, 3, 'customer', 'I''m having trouble installing Python on Windows 11. Any suggestions?', 0),
(2, 14, 1, 'customer', 'How do I efficiently filter rows in a DataFrame?', 1),
(3, 21, 1, 'customer', 'Getting a bundler error when trying to run the app. Help needed!', 0),
(4, 31, 2, 'customer', 'What''s the best approach for implementing player movement in Unity?', 1),
(4, 35, 3, 'customer', 'How do I optimize my game for mobile devices?', 0),
(5, 41, 2, 'customer', 'What color combinations work best for UI design?', 1),
(6, 45, 3, 'customer', 'Can someone explain JavaScript closures with a simple example?', 0),
(7, 13, 2, 'customer', 'My model has low accuracy. How can I improve it?', 0),
(8, 14, 1, 'customer', 'When should I use SwiftUI instead of UIKit?', 1),
(1, 5, 4, 'customer', 'How do we make a responsive design work on all screen sizes?', 0),
(1, 6, 5, 'customer', 'Is React better than Angular for beginners?', 1),
(2, 16, 6, 'customer', 'Which visualization library is better - Matplotlib or Seaborn?', 0),
(3, 23, 7, 'customer', 'My app crashes when I try to implement navigation. Any help?', 0),
(4, 33, 8, 'customer', 'What''s the difference between rigid body and character controller?', 1),
(5, 43, 9, 'customer', 'How do I conduct effective usability testing with limited resources?', 0),
(6, 48, 10, 'customer', 'Can someone explain the concept of "this" in JavaScript?', 1),
(7, 13, 11, 'customer', 'Which algorithm works best for image classification tasks?', 0),
(9, 13, 12, 'customer', 'How do I optimize complex SQL queries?', 1),
(3, 22, 1, 'customer', 'I''m struggling with state management in React Native. What''s the best approach for a complex app?', 0),
(3, 24, 3, 'customer', 'How do I implement push notifications in React Native?', 0),
(3, 25, 5, 'customer', 'My React Native app performs poorly on older Android devices. Any optimization tips?', 0),
(3, 26, 2, 'customer', 'What''s the best way to handle image caching in React Native?', 1),
(3, 27, 4, 'customer', 'I''m having issues with React Native animations. They seem choppy on Android.', 0),
(3, 28, 6, 'customer', 'How can I implement deep linking in my React Native app?', 1),
(3, 29, 8, 'customer', 'What''s the recommended approach for offline data storage in React Native?', 0),
(3, 30, 10, 'customer', 'How do I handle different screen sizes in React Native?', 0),
(8, 14, 3, 'customer', 'What are the main differences between UIKit and SwiftUI for beginners?', 0),
(8, 15, 5, 'customer', 'How do I implement Core Data in a SwiftUI project?', 1),
(8, 16, 7, 'customer', 'My Swift app crashes when parsing JSON from an API. How can I debug this?', 0),
(8, 17, 9, 'customer', 'What''s the best practice for handling user authentication in iOS apps?', 0),
(8, 18, 11, 'customer', 'How do I implement dark mode support in my Swift app?', 1),
(8, 19, 13, 'customer', 'I''m having trouble with Auto Layout constraints. Any debugging tips?', 0),
(6, 46, 12, 'customer', 'What is the difference between null and undefined in JavaScript?', 0),
(6, 47, 14, 'customer', 'Can someone explain how prototype inheritance works in JavaScript?', 0),
(6, 49, 8, 'customer', 'What''s the best way to handle errors in async JavaScript functions?', 0),
(6, 50, 10, 'customer', 'Could someone explain the key array methods like map, filter, and reduce?', 0),
(6, 51, 7, 'customer', 'How do you mock API calls when writing JavaScript tests?', 0),
(15, 140, 11, 'customer', 'Should I learn Vue 2 or Vue 3 as a beginner?', 0),
(15, 143, 13, 'customer', 'How does Vue''s reactivity system work under the hood?', 0),
(15, 145, 9, 'customer', 'My Vuex mutations aren''t updating state, what am I doing wrong?', 0),
(15, 148, 5, 'customer', 'What''s the best folder structure for a large Vue application?', 0),
(15, 149, 4, 'customer', 'What are the best practices for deploying a Vue application?', 0),
(16, 150, 2, 'customer', 'What''s the difference between a Django project and a Django app?', 0),
(16, 152, 6, 'customer', 'How do I create custom template tags in Django?', 0),
(16, 154, 3, 'customer', 'What are the best ways to customize the Django admin interface?', 0),
(16, 156, 1, 'customer', 'What authentication options are available in Django REST Framework?', 0),
(16, 158, 7, 'customer', 'What''s the recommended way to deploy a Django application?', 0);

-- Insert DiscussionReplies
INSERT INTO DiscussionReplies (DiscussionID, AuthorID, AuthorType, Content)
VALUES
(1, 4, 'customer', '<div> is a block element while <span> is an inline element. Use <div> when you want a new line before and after the element, and <span> when you want to style text inline.'),
(2, 4, 'customer', 'for...in loops iterate over the enumerable properties of an object, while for...of loops iterate over the values of an iterable object like arrays.'),
(1, 2, 'customer', 'Also, <div> is commonly used for layout while <span> is used for styling small portions of text.'),
(3, 5, 'customer', 'Try downloading the latest installer from python.org and make sure to check "Add Python to PATH" during installation.'),
(5, 2, 'customer', 'Check if your Metro bundler is running. You might need to restart it with "npx react-native start".'),
(4, 5, 'customer', 'You can use df.loc[] for label-based filtering or df.query() for string expressions.'),
(4, 3, 'customer', 'Thanks! df.query() worked perfectly for my needs.'),
(6, 10, 'customer', 'For game UIs, keep controls intuitive and consistent. Use clear visual feedback for player actions and maintain a balanced HUD that doesn''t obstruct gameplay.'),
(7, 10, 'customer', 'For mobile optimization: 1) Reduce texture sizes 2) Use LOD groups 3) Minimize draw calls 4) Use mobile-specific quality settings 5) Profile performance regularly'),
(9, 4, 'customer', 'Closures are functions that remember their lexical environment. Example: function outer() { let x = 10; function inner() { console.log(x); } return inner; }'),
(10, 5, 'customer', 'Try feature engineering, hyperparameter tuning, or using a more complex model. Also check if your data is imbalanced.'),
(11, 4, 'customer', 'Use SwiftUI for new apps targeting iOS 13+ for faster development. UIKit is better for complex UI, backward compatibility, or specific controls not available in SwiftUI.'),
(12, 4, 'customer', 'Use media queries in CSS to target different screen sizes. Start with a mobile-first approach and progressively enhance for larger screens.'),
(13, 8, 'customer', 'For beginners, React often has a gentler learning curve compared to Angular, which has more concepts to master initially.'),
(13, 3, 'customer', 'I agree. I found React much easier to get started with compared to Angular.'),
(14, 5, 'customer', 'Both have their strengths. Matplotlib offers more control, while Seaborn provides more attractive visualizations with less code. Start with Seaborn for simple plots.'),
(15, 4, 'customer', 'Make sure you''ve properly installed react-navigation and its dependencies. Check the navigator structure for common mistakes like missing screenOptions.'),
(16, 10, 'customer', 'Rigid bodies are physics-based and affected by forces, while character controllers are designed specifically for character movement with manual control over physics.'),
(17, 6, 'customer', 'For limited budgets, try guerrilla testing with 5-7 users, remote testing tools, or A/B testing on a live but limited audience segment.'),
(18, 4, 'customer', 'The "this" keyword refers to the object it belongs to. In regular functions, "this" depends on how the function is called. In arrow functions, "this" retains the value from the enclosing context.'),
(19, 5, 'customer', 'For image classification, CNNs are the go-to. Specifically, architectures like ResNet, EfficientNet, or Vision Transformer work well depending on your needs and computational constraints.'),
(20, 8, 'customer', 'For SQL optimization: 1) Use appropriate indexes 2) Avoid SELECT * 3) Use EXPLAIN to analyze your queries 4) Consider denormalization for read-heavy operations 5) Use stored procedures for complex operations'),
(1, 1, 'customer', 'Great question! <div> is a block-level element that takes up the full width available and creates a new line before and after it. <span> is an inline element that only takes up as much width as necessary and doesn''t force new lines. Use <div> for layout sections and <span> for styling pieces of text within a block.'),
(2, 1, 'customer', 'The for...of loop iterates over the values of iterable objects like arrays, strings, etc. The for...in loop iterates over the enumerable properties of an object. Be careful with for...in on arrays as it will include properties added to the array''s prototype!'),
(12, 1, 'customer', 'Good question! For responsive design, use a combination of: 1) Media queries to apply different styles at different breakpoints 2) Flexbox or Grid for layout 3) Relative units like % or rem instead of fixed pixels 4) viewport units (vw, vh) and 5) the meta viewport tag in your HTML. Test on multiple devices as you develop.'),
(13, 1, 'customer', 'I generally recommend React for beginners because it has a simpler mental model and faster learning curve. Angular has more built-in features but comes with more complexity. React''s component model is very intuitive, and the community support is excellent for learning resources.'),
(9, 1, 'customer', 'A closure is a function that remembers the variables from the scope where it was created, even after that scope has closed. Here''s a simple example: function createCounter() { let count = 0; return function() { return ++count; }; } const counter = createCounter(); console.log(counter()); // 1 console.log(counter()); // 2 The inner function "closes over" the count variable.'),
(18, 1, 'customer', 'Great question about "this" in JavaScript! It refers to the object that is executing the current function. In regular functions, "this" is determined by how the function is called (its execution context). In arrow functions, "this" retains the value from the enclosing lexical scope, which makes them useful for callbacks inside methods.'),
(35, 1, 'customer', 'Null and undefined are both used to represent "no value" but in different ways. Undefined means a variable has been declared but not assigned a value, while null is an intentional assignment representing "no value" or "empty". For example, if you want to explicitly clear a variable, use null. If you check a property that doesn''t exist, you''ll get undefined.'),
(36, 1, 'customer', 'The prototype chain in JavaScript is how inheritance works. Each object has a prototype (accessible via Object.getPrototypeOf() or the deprecated __proto__ property) from which it inherits properties. When you access a property, JavaScript first looks on the object itself, then its prototype, then its prototype''s prototype, and so on until it finds the property or reaches the end of the chain.'),
(37, 1, 'customer', 'For handling errors in async functions, always use try/catch blocks. Example: async function fetchData() { try { const response = await api.getData(); return response; } catch (error) { console.error(''Failed to fetch data:'', error); // Handle error appropriately throw error; // Re-throw if needed } } You can also use .catch() with promises if you prefer that syntax.'),
(38, 1, 'customer', 'Array methods explained: map() transforms each element and returns a new array of the same length; filter() creates a new array with elements that pass a test; reduce() accumulates values to a single result. Example: const numbers = [1, 2, 3, 4]; const doubled = numbers.map(x => x * 2); // [2, 4, 6, 8] const evens = numbers.filter(x => x % 2 === 0); // [2, 4] const sum = numbers.reduce((acc, x) => acc + x, 0); // 10'),
(39, 1, 'customer', 'For mocking API calls in tests, you have several options: 1) Use Jest''s mock functions to replace fetch/axios, 2) Use libraries like axios-mock-adapter, 3) Use fetch-mock or similar, or 4) Use MSW (Mock Service Worker) for a more modern approach. The key is intercepting the network requests and providing controlled responses to test both success and error scenarios.'),
(40, 1, 'customer', 'I recommend learning Vue 3 directly as it''s the future of Vue. The Composition API in Vue 3 offers better TypeScript support and code organization for complex applications. However, many existing projects and tutorials use Vue 2, so understanding both can be valuable. The core concepts are the same, so learning Vue 3 won''t make it difficult to work with Vue 2 code if needed.'),
(41, 1, 'customer', 'Reactivity in Vue 3 uses the Composition API with ref() and reactive(). ref() is for primitive values (creating a reactive reference object with a .value property), while reactive() is for objects (creating a reactive proxy of the entire object). Vue needs these special functions to track property access and changes, which is how it knows when to update the DOM.'),
(42, 1, 'customer', 'If your Vuex mutations aren''t updating state, check these common issues: 1) Make sure you''re using commit to call mutations, not direct state modification, 2) Verify mutation names match exactly, 3) Check if you''re modifying nested objects (Vue can''t detect these changes directly), 4) Use Vue devtools to see if mutations are being called, and 5) Ensure you''re not trying to modify state outside mutations.'),
(43, 1, 'customer', 'For large Vue applications, I recommend: 1) Feature-based structure instead of type-based, 2) Modules for Vuex store, 3) Component registration for common components, 4) Router with lazy-loading, 5) Services for API calls, and 6) Composables (in Vue 3) for shared logic. Example structure: src/features/auth/, src/features/products/, src/components/common/, src/router/, src/store/, src/services/, etc.'),
(44, 1, 'customer', 'To deploy a production Vue app: 1) Run npm run build to create optimized files, 2) Enable Vue Router''s history mode with proper server config, 3) Use dynamic imports for code-splitting (import() syntax), 4) Lazy load routes, 5) Configure proper cache headers for static assets, 6) Consider using a CDN for distribution, and 7) Use performance monitoring tools like Lighthouse to verify optimizations.'),
(45, 1, 'customer', 'Great question! A Django project is the entire web application, containing multiple apps and configuration. A Django app is a self-contained module within a project that handles a specific functionality (e.g., blog, authentication, etc.). Projects can contain multiple apps, and apps can be reused across different projects. Think of a project as the website and apps as the features within it.'),
(46, 1, 'customer', 'To create custom template tags in Django: 1) Create a templatetags package in your app with an __init__.py file, 2) Create a Python module (e.g., custom_tags.py), 3) Use @register.simple_tag or @register.inclusion_tag decorators, 4) Load your tags in templates with {% load custom_tags %}. Example: from django import template\nregister = template.Library()\n@register.simple_tag\ndef multiply(a, b):\n    return a * b'),
(47, 1, 'customer', 'To customize Django admin: 1) Create a custom ModelAdmin class in admin.py, 2) Use list_display, list_filter, search_fields for customized lists, 3) Override get_urls() for custom views, 4) Use admin actions for batch operations, 5) Create custom templates in templates/admin/. Example: @admin.register(Product)\nclass ProductAdmin(admin.ModelAdmin):\n    list_display = [''name'', ''price'', ''in_stock'']\n    actions = [''mark_as_featured'']'),
(48, 1, 'customer', 'For Django REST Framework authentication, I recommend: 1) Token authentication for mobile apps, 2) JWT for SPAs with refresh tokens, 3) Session auth for server-rendered Django apps, 4) OAuth2 for third-party integration. Always use permission_classes to restrict access based on user roles. For social auth, django-allauth or social-auth-app-django are excellent options.'),
(49, 1, 'customer', 'For deploying Django: 1) Use Gunicorn/uWSGI as the app server, 2) Nginx as the web server, 3) PostgreSQL for the database, 4) Redis for caching, 5) Use environment variables for settings, 6) Collect static files with collectstatic, 7) Use a CDN for static files, and 8) Consider managed services like AWS RDS for databases. Docker containers are also excellent for consistent deployments.');

-- Insert Videos
INSERT INTO Videos (LessonID, Title, Description, VideoUrl, Duration)
VALUES
-- Web Development Bootcamp (CourseID 1)
(1, 'HTML Structure and Elements', 'Learn the basic structure of HTML documents and essential elements', '/assets/videos/html-structure.mp4', 1800),
(2, 'CSS Selectors and Properties', 'Master CSS selectors and common styling properties', '/assets/videos/css-selectors.mp4', 2100),
(3, 'JavaScript Variables and Functions', 'Understanding variables, functions, and scope in JavaScript', '/assets/videos/js-basics.mp4', 2400),
(4, 'Building Your First Web Page', 'Hands-on tutorial for creating a complete web page', '/assets/videos/first-webpage.mp4', 3000),
(5, 'Media Queries and Flexbox', 'How to create responsive layouts with CSS', '/assets/videos/responsive-design.mp4', 2700),
(6, 'React Components and Props', 'Introduction to React component architecture', '/assets/videos/react-intro.mp4', 2500),
(7, 'State Management in React', 'Managing and updating state in React applications', '/assets/videos/react-state.mp4', 2600),
(8, 'Node.js and Express Framework', 'Server-side JavaScript with Node.js', '/assets/videos/nodejs-basics.mp4', 2400),
(9, 'Creating RESTful APIs', 'Design and implement RESTful APIs with Express', '/assets/videos/restful-apis.mp4', 2700),
(10, 'MongoDB Integration', 'Connecting and using MongoDB with Node.js', '/assets/videos/mongodb-integration.mp4', 2500),
(11, 'Deploying to Heroku and Netlify', 'How to deploy your full-stack applications', '/assets/videos/deployment.mp4', 2200),
(12, 'Final Project Walkthrough', 'Step-by-step guide for the course final project', '/assets/videos/final-project-web.mp4', 3600),
-- Python for Data Science (CourseID 2)
(13, 'Python Syntax and Data Types', 'Introduction to Python programming language', '/assets/videos/python-basics.mp4', 2400),
(14, 'Pandas DataFrame Operations', 'Working with data using pandas library', '/assets/videos/pandas-intro.mp4', 2700),
(15, 'Creating Charts with Matplotlib', 'Data visualization techniques in Python', '/assets/videos/matplotlib-intro.mp4', 2500),
(16, 'Statistical Analysis Methods', 'Applying statistical methods to datasets', '/assets/videos/stats-analysis.mp4', 2800),
(17, 'Introduction to Machine Learning Models', 'Overview of common ML algorithms', '/assets/videos/scikit-learn-intro.mp4', 3000),
(18, 'Data Cleaning Techniques', 'Methods for preparing and cleaning datasets', '/assets/videos/data-cleaning.mp4', 2400),
(19, 'Working with REST APIs', 'Fetching and processing data from APIs', '/assets/videos/python-apis.mp4', 2200),
(20, 'Processing Large Datasets', 'Techniques for handling big data in Python', '/assets/videos/big-data-python.mp4', 2600),
-- React Native Mobile Apps (CourseID 3)
(21, 'Setting Up React Native Environment', 'Guide to setting up React Native development environment', '/assets/videos/react-native-setup.mp4', 179),
(22, 'Building Your First React Native App', 'Step-by-step app creation tutorial', '/assets/videos/react-native-first-app.mp4', 350),
(23, 'Navigation in React Native', 'Implementing navigation using React Navigation', '/assets/videos/react-native-navigation.mp4', 175),
(24, 'State Management with Redux', 'Managing app state with Redux', '/assets/videos/react-native-redux.mp4', 173),
(25, 'Using Native Components', 'Integrating device-native features', '/assets/videos/react-native-native-components.mp4', 178),
(26, 'Handling User Input', 'Managing forms and user interactions', '/assets/videos/react-native-user-input.mp4', 131),
(27, 'Networking with APIs', 'Connecting to REST APIs in React Native', '/assets/videos/react-native-networking.mp4', 169),
(28, 'Deploying to App Stores', 'Publishing apps to Google Play and App Store', '/assets/videos/react-native-deployment.mp4', 179),
(29, 'Performance Optimization', 'Optimizing React Native apps for performance', '/assets/videos/react-native-performance.mp4', 164),
(30, 'Final Project Walkthrough', 'Building a complete React Native app', '/assets/videos/react-native-final-project.mp4', 167),
-- Game Development with Unity (CourseID 4)
(31, 'Unity Interface Overview', 'Introduction to Unity editor and tools', '/assets/videos/unity-intro.mp4', 2000),
(32, 'Game Objects and Components', 'Working with Unity game objects', '/assets/videos/unity-game-objects.mp4', 2200),
(33, 'C# Scripting Basics', 'Learning C# for Unity game development', '/assets/videos/unity-csharp.mp4', 2500),
(34, 'Game Physics', 'Implementing physics and collisions in Unity', '/assets/videos/unity-physics.mp4', 2400),
(35, 'Game UI Design', 'Creating user interfaces in Unity', '/assets/videos/unity-ui.mp4', 2300),
(36, 'Audio Integration', 'Adding sound effects and music to games', '/assets/videos/unity-audio.mp4', 2100),
(37, 'Character Animation', 'Animating characters in Unity', '/assets/videos/unity-animation.mp4', 2600),
(38, 'Game AI Basics', 'Implementing basic AI behaviors', '/assets/videos/unity-ai.mp4', 2400),
(39, 'Performance Optimization', 'Optimizing games for better performance', '/assets/videos/unity-performance.mp4', 2200),
(40, 'Publishing Your Game', 'Steps to publish your Unity game', '/assets/videos/unity-publishing.mp4', 2000),
-- UI/UX Design Principles (CourseID 5)
(41, 'Design Thinking Basics', 'Introduction to design thinking principles', '/assets/videos/design-thinking.mp4', 2100),
(42, 'User Research Techniques', 'Conducting effective user research', '/assets/videos/user-research.mp4', 2300),
(43, 'Wireframing Tools', 'Creating wireframes for UI design', '/assets/videos/wireframing.mp4', 2200),
(44, 'Color Theory Basics', 'Understanding color theory for UI', '/assets/videos/color-theory.mp4', 2000),
(45, 'Typography Essentials', 'Choosing and applying typography', '/assets/videos/typography.mp4', 2100),
(46, 'Creating User Personas', 'Building effective user personas', '/assets/videos/user-personas.mp4', 2000),
(47, 'Usability Testing Methods', 'Conducting usability tests', '/assets/videos/usability-testing.mp4', 2300),
(48, 'Mobile-First Design', 'Designing for mobile devices first', '/assets/videos/mobile-first.mp4', 2200),
-- Advanced JavaScript (CourseID 6)
(49, 'Introduce Advanced JavaScript', 'Build more complex, efficient, and scalable web applications', '/assets/videos/js-advanced.mp4', 2400),
(50, 'Understanding Closures', 'Deep dive into JavaScript closures', '/assets/videos/js-closures.mp4', 2400),
(51, 'Prototypes and Inheritance', 'Exploring JavaScript prototypes', '/assets/videos/js-prototypes.mp4', 2500),
(52, 'Asynchronous Programming', 'Handling async operations in JavaScript', '/assets/videos/js-async.mp4', 2600),
(53, 'ES6+ Features', 'Modern JavaScript features and syntax', '/assets/videos/js-es6.mp4', 2300),
(54, 'Functional Programming', 'Applying functional programming in JS', '/assets/videos/js-functional.mp4', 2400),
(55, 'Design Patterns', 'Common JavaScript design patterns', '/assets/videos/js-patterns.mp4', 2500),
(56, 'Testing JavaScript', 'Unit testing with Jest', '/assets/videos/js-testing.mp4', 2200),
(57, 'Performance Optimization', 'Optimizing JavaScript code', '/assets/videos/js-performance.mp4', 2300),
(58, 'Advanced Project Walkthrough', 'Building a complex JS project', '/assets/videos/js-advanced-project.mp4', 3600),
-- Machine Learning Fundamentals (CourseID 7)
(59, 'ML Concepts Overview', 'Introduction to machine learning concepts', '/assets/videos/ml-intro.mp4', 2400),
(60, 'Supervised Learning', 'Understanding supervised learning algorithms', '/assets/videos/ml-supervised.mp4', 2600),
(61, 'Unsupervised Learning', 'Exploring unsupervised learning techniques', '/assets/videos/ml-unsupervised.mp4', 2500),
(62, 'Feature Engineering', 'Crafting effective features for ML', '/assets/videos/ml-features.mp4', 2400),
(63, 'Model Evaluation', 'Evaluating ML model performance', '/assets/videos/ml-evaluation.mp4', 2300),
(64, 'Neural Networks Intro', 'Basics of neural networks', '/assets/videos/ml-neural.mp4', 2600),
(65, 'Deep Learning Basics', 'Introduction to deep learning', '/assets/videos/ml-deep-learning.mp4', 2700),
(66, 'ML Applications', 'Real-world machine learning applications', '/assets/videos/ml-applications.mp4', 2500),
(67, 'AI Ethics', 'Ethical considerations in machine learning', '/assets/videos/ml-ethics.mp4', 2200),
(68, 'Final ML Project', 'Building a complete ML project', '/assets/videos/ml-final-project.mp4', 3600),
-- iOS App Development with Swift (CourseID 8)
(69, 'Swift Language Basics', 'Introduction to Swift programming', '/assets/videos/swift-basics.mp4', 2400),
(70, 'iOS App Architecture', 'Understanding iOS app structure', '/assets/videos/ios-architecture.mp4', 2300),
(71, 'UIKit Fundamentals', 'Working with UIKit for UI', '/assets/videos/uikit-basics.mp4', 2500),
(72, 'SwiftUI Basics', 'Introduction to SwiftUI framework', '/assets/videos/swiftui-intro.mp4', 2400),
(73, 'Data Management', 'Handling data in iOS apps', '/assets/videos/ios-data.mp4', 2300),
(74, 'Networking in iOS', 'Connecting to APIs in iOS', '/assets/videos/ios-networking.mp4', 2600),
(75, 'Core Data Basics', 'Using Core Data for persistence', '/assets/videos/core-data.mp4', 2500),
(76, 'iOS Testing', 'Testing iOS applications', '/assets/videos/ios-testing.mp4', 2200),
(77, 'App Store Deployment', 'Publishing to the App Store', '/assets/videos/ios-deployment.mp4', 2300),
(78, 'Final iOS Project', 'Building a complete iOS app', '/assets/videos/ios-final-project.mp4', 3600),
-- SQL Database Mastery (CourseID 9)
(79, 'Database Fundamentals', 'Introduction to database concepts', '/assets/videos/db-intro.mp4', 2100),
(80, 'SQL CRUD Operations', 'Basic SQL operations', '/assets/videos/sql-crud.mp4', 2300),
(81, 'Advanced SQL Queries', 'Writing complex SQL queries', '/assets/videos/sql-advanced.mp4', 2500),
(82, 'Database Design', 'Designing normalized databases', '/assets/videos/db-design.mp4', 2400),
(83, 'Indexing Techniques', 'Optimizing database performance', '/assets/videos/db-indexing.mp4', 2200),
(84, 'Stored Procedures', 'Writing stored procedures', '/assets/videos/db-procedures.mp4', 2300),
(85, 'Transactions', 'Managing database transactions', '/assets/videos/db-transactions.mp4', 2200),
(86, 'Database Security', 'Securing database systems', '/assets/videos/db-security.mp4', 2100),
(87, 'Real-world DB Projects', 'Building practical database solutions', '/assets/videos/db-projects.mp4', 3000),
-- DevOps Engineering (CourseID 10)
(88, 'DevOps Introduction', 'Overview of DevOps principles', '/assets/videos/devops-intro.mp4', 2100),
(89, 'Git Version Control', 'Using Git for version control', '/assets/videos/devops-git.mp4', 2300),
(90, 'CI/CD Pipelines', 'Setting up CI/CD pipelines', '/assets/videos/devops-cicd.mp4', 2500),
(91, 'Docker Containers', 'Containerization with Docker', '/assets/videos/devops-docker.mp4', 2400),
(92, 'Kubernetes Basics', 'Orchestrating containers with Kubernetes', '/assets/videos/devops-kubernetes.mp4', 2600),
(93, 'Infrastructure as Code', 'Using IaC tools like Terraform', '/assets/videos/devops-iac.mp4', 2300),
(94, 'Monitoring Systems', 'Setting up monitoring and logging', '/assets/videos/devops-monitoring.mp4', 2200),
(95, 'Cloud Integration', 'Integrating with cloud services', '/assets/videos/devops-cloud.mp4', 2400),
(96, 'DevOps Best Practices', 'Implementing DevOps effectively', '/assets/videos/devops-practices.mp4', 2300),
(97, 'DevOps Project', 'Complete DevOps pipeline project', '/assets/videos/devops-project.mp4', 3600),
-- Ethical Hacking (CourseID 11)
(98, 'Ethical Hacking Intro', 'Introduction to ethical hacking', '/assets/videos/hacking-intro.mp4', 2100),
(99, 'Reconnaissance', 'Gathering information for hacking', '/assets/videos/hacking-recon.mp4', 2300),
(100, 'Network Scanning', 'Scanning networks for vulnerabilities', '/assets/videos/hacking-scanning.mp4', 2400),
(101, 'Enumeration Techniques', 'Enumerating system resources', '/assets/videos/hacking-enumeration.mp4', 2200),
(102, 'Vulnerability Analysis', 'Identifying system vulnerabilities', '/assets/videos/hacking-vulnerabilities.mp4', 2300),
(103, 'System Hacking', 'Exploiting system weaknesses', '/assets/videos/hacking-system.mp4', 2500),
(104, 'Malware Analysis', 'Understanding malware threats', '/assets/videos/hacking-malware.mp4', 2400),
(105, 'Network Sniffing', 'Capturing network traffic', '/assets/videos/hacking-sniffing.mp4', 2200),
(106, 'Social Engineering', 'Exploiting human psychology', '/assets/videos/hacking-social.mp4', 2100),
(107, 'DoS Attacks', 'Understanding denial-of-service attacks', '/assets/videos/hacking-dos.mp4', 2300),
(108, 'Session Hijacking', 'Hijacking user sessions', '/assets/videos/hacking-session.mp4', 2200),
(109, 'Web Security', 'Securing web applications', '/assets/videos/hacking-web.mp4', 2400),
-- Deep Learning with TensorFlow (CourseID 12)
(110, 'Deep Learning Intro', 'Introduction to deep learning concepts', '/assets/videos/dl-intro.mp4', 2400),
(111, 'TensorFlow Basics', 'Getting started with TensorFlow', '/assets/videos/dl-tensorflow.mp4', 2500),
(112, 'Building Neural Networks', 'Constructing neural networks', '/assets/videos/dl-neural-networks.mp4', 2600),
(113, 'Convolutional Neural Networks', 'Understanding CNNs', '/assets/videos/dl-cnn.mp4', 2700),
(114, 'Recurrent Neural Networks', 'Working with RNNs', '/assets/videos/dl-rnn.mp4', 2500),
(115, 'Transfer Learning', 'Using pre-trained models', '/assets/videos/dl-transfer.mp4', 2400),
(116, 'Generative Models', 'Creating generative AI models', '/assets/videos/dl-generative.mp4', 2600),
(117, 'NLP with TensorFlow', 'Natural language processing basics', '/assets/videos/dl-nlp.mp4', 2500),
(118, 'Computer Vision', 'Applying deep learning to vision', '/assets/videos/dl-vision.mp4', 2700),
(119, 'Model Deployment', 'Deploying TensorFlow models', '/assets/videos/dl-deployment.mp4', 2300),
-- Blockchain Development (CourseID 13)
(120, 'Blockchain Basics', 'Introduction to blockchain technology', '/assets/videos/blockchain-intro.mp4', 2200),
(121, 'Cryptography Fundamentals', 'Understanding cryptographic principles', '/assets/videos/blockchain-crypto.mp4', 2300),
(122, 'Bitcoin Protocol', 'Exploring Bitcoin blockchain', '/assets/videos/blockchain-bitcoin.mp4', 2400),
(123, 'Ethereum Smart Contracts', 'Building smart contracts with Ethereum', '/assets/videos/blockchain-ethereum.mp4', 2600),
(124, 'Solidity Programming', 'Learning Solidity for smart contracts', '/assets/videos/blockchain-solidity.mp4', 2500),
(125, 'Building DApps', 'Creating decentralized applications', '/assets/videos/blockchain-dapps.mp4', 2700),
(126, 'Web3.js Integration', 'Connecting blockchain to frontend', '/assets/videos/blockchain-web3.mp4', 2400),
(127, 'Testing Smart Contracts', 'Ensuring smart contract reliability', '/assets/videos/blockchain-testing.mp4', 2300),
(128, 'Blockchain Security', 'Securing blockchain applications', '/assets/videos/blockchain-security.mp4', 2200),
(129, 'Blockchain Project', 'Building a complete blockchain app', '/assets/videos/blockchain-project.mp4', 3600),
-- Internet of Things Fundamentals (CourseID 14)
(130, 'IoT Overview', 'Introduction to IoT concepts', '/assets/videos/iot-intro.mp4', 2100),
(131, 'Sensors and Actuators', 'Working with IoT hardware', '/assets/videos/iot-sensors.mp4', 2300),
(132, 'Arduino Programming', 'Programming Arduino for IoT', '/assets/videos/iot-arduino.mp4', 2400),
(133, 'Raspberry Pi Setup', 'Setting up Raspberry Pi for IoT', '/assets/videos/iot-raspberry.mp4', 2500),
(134, 'IoT Networking', 'Networking protocols for IoT', '/assets/videos/iot-networking.mp4', 2300),
(135, 'IoT Cloud Platforms', 'Using cloud services for IoT', '/assets/videos/iot-cloud.mp4', 2400),
(136, 'IoT Security', 'Securing IoT devices', '/assets/videos/iot-security.mp4', 2200),
(137, 'IoT Data Analytics', 'Analyzing IoT data', '/assets/videos/iot-analytics.mp4', 2300),
(138, 'End-to-End IoT', 'Building complete IoT solutions', '/assets/videos/iot-end-to-end.mp4', 2600),
(139, 'IoT Industry Applications', 'Real-world IoT applications', '/assets/videos/iot-applications.mp4', 2400),
-- Vue.js for Frontend Development (CourseID 15)
(140, 'Vue.js Introduction', 'Getting started with Vue.js', '/assets/videos/vue-intro.mp4', 2100),
(141, 'Vue Components', 'Building reusable Vue components', '/assets/videos/vue-components.mp4', 2300),
(142, 'Vue Directives', 'Using Vue directives and events', '/assets/videos/vue-directives.mp4', 2200),
(143, 'Vue Reactivity', 'Understanding Vue reactivity system', '/assets/videos/vue-reactivity.mp4', 2400),
(144, 'Vue Router', 'Implementing navigation with Vue Router', '/assets/videos/vue-router.mp4', 2300),
(145, 'Vuex State Management', 'Managing state with Vuex', '/assets/videos/vue-vuex.mp4', 2500),
(146, 'Vue Forms', 'Handling forms in Vue.js', '/assets/videos/vue-forms.mp4', 2200),
(147, 'Vue Best Practices', 'Best practices for Vue development', '/assets/videos/vue-practices.mp4', 2300),
(148, 'Testing Vue Apps', 'Unit testing Vue applications', '/assets/videos/vue-testing.mp4', 2200),
(149, 'Vue Production App', 'Building a production-ready Vue app', '/assets/videos/vue-project.mp4', 3600),
-- Django Web Framework (CourseID 16)
(150, 'Django Introduction', 'Overview of Django framework', '/assets/videos/django-intro.mp4', 2100),
(151, 'Django Models', 'Working with Django models and databases', '/assets/videos/django-models.mp4', 2300),
(152, 'Django Views', 'Creating views and templates in Django', '/assets/videos/django-views.mp4', 2400),
(153, 'Django Forms', 'Handling forms in Django', '/assets/videos/django-forms.mp4', 2200),
(154, 'Django Admin', 'Customizing Django admin interface', '/assets/videos/django-admin.mp4', 2300),
(155, 'Django Authentication', 'Implementing authentication in Django', '/assets/videos/django-auth.mp4', 2400),
(156, 'Django REST APIs', 'Building APIs with Django REST Framework', '/assets/videos/django-rest.mp4', 2500),
(157, 'Testing Django Apps', 'Unit testing Django applications', '/assets/videos/django-testing.mp4', 2200),
(158, 'Django Deployment', 'Deploying Django applications', '/assets/videos/django-deployment.mp4', 2300),
(159, 'Django Project', 'Building a complete Django project', '/assets/videos/django-project.mp4', 3600);

-- Insert Materials
INSERT INTO Materials (LessonID, Title, Description, FileUrl)
VALUES
-- Web Development Bootcamp (CourseID 1)
(1, 'HTML Cheat Sheet', 'Quick reference for HTML elements and attributes', '/assets/materials/html-cheat-sheet.pdf'),
(2, 'CSS Properties Guide', 'Comprehensive guide to CSS properties', '/assets/materials/css-guide.pdf'),
(3, 'JavaScript Basics', 'Summary of JavaScript fundamentals', '/assets/materials/js-basics.pdf'),
(4, 'Website Project Template', 'Starter template for building a website', '/assets/materials/website-template.zip'),
(5, 'Responsive Design Guide', 'Best practices for responsive design', '/assets/materials/responsive-design.pdf'),
(6, 'React Component Guide', 'Guide to React components and props', '/assets/materials/react-components.pdf'),
(7, 'React State Management', 'State management patterns in React', '/assets/materials/react-state.pdf'),
(8, 'Node.js Cheat Sheet', 'Quick reference for Node.js APIs', '/assets/materials/nodejs-cheat-sheet.pdf'),
(9, 'REST API Design', 'Best practices for RESTful APIs', '/assets/materials/rest-api-design.pdf'),
(10, 'MongoDB Guide', 'Introduction to MongoDB integration', '/assets/materials/mongodb-guide.pdf'),
(11, 'Deployment Checklist', 'Checklist for deploying web apps', '/assets/materials/deployment-checklist.pdf'),
(12, 'Final Project Guidelines', 'Guidelines for the final project', '/assets/materials/web-final-project.pdf'),
-- Python for Data Science (CourseID 2)
(13, 'Python Cheat Sheet', 'Quick reference for Python syntax', '/assets/materials/python-cheat-sheet.pdf'),
(14, 'Pandas Reference', 'Pandas operations and functions', '/assets/materials/pandas-reference.pdf'),
(15, 'Matplotlib Guide', 'Guide to creating visualizations', '/assets/materials/matplotlib-guide.pdf'),
(16, 'Statistics Cheat Sheet', 'Key statistical methods', '/assets/materials/stats-cheat-sheet.pdf'),
(17, 'Scikit-learn Reference', 'Overview of Scikit-learn APIs', '/assets/materials/scikit-learn-reference.pdf'),
(18, 'Data Cleaning Checklist', 'Checklist for data cleaning', '/assets/materials/data-cleaning-checklist.pdf'),
(19, 'API Integration Guide', 'Guide to working with APIs', '/assets/materials/api-integration.pdf'),
(20, 'Big Data Processing', 'Techniques for big data in Python', '/assets/materials/big-data-python.pdf'),
-- React Native Mobile Apps (CourseID 3)
(21, 'React Native Setup Guide', 'Step-by-step setup instructions', '/assets/materials/react-native-setup.pdf'),
(22, 'App Template', 'Starter template for React Native', '/assets/materials/react-native-template.zip'),
(23, 'Navigation Guide', 'React Navigation best practices', '/assets/materials/navigation-guide.pdf'),
(24, 'Redux Reference', 'Redux state management guide', '/assets/materials/redux-reference.pdf'),
(25, 'Native Components', 'List of native components', '/assets/materials/native-components.pdf'),
(26, 'Form Handling Guide', 'Best practices for forms', '/assets/materials/form-handling.pdf'),
(27, 'API Integration', 'Connecting to APIs in React Native', '/assets/materials/api-integration-rn.pdf'),
(28, 'App Store Guidelines', 'Guidelines for app store submission', '/assets/materials/app-store-guidelines.pdf'),
(29, 'Performance Checklist', 'Optimizing React Native apps', '/assets/materials/performance-checklist.pdf'),
(30, 'Final Project Specs', 'Specifications for final project', '/assets/materials/rn-final-project.pdf'),
-- Game Development with Unity (CourseID 4)
(31, 'Unity Interface Guide', 'Overview of Unity editor', '/assets/materials/unity-interface.pdf'),
(32, 'Game Objects Reference', 'Guide to Unity game objects', '/assets/materials/game-objects.pdf'),
(33, 'C# Cheat Sheet', 'Quick reference for C# in Unity', '/assets/materials/csharp-cheat-sheet.pdf'),
(34, 'Physics Guide', 'Unity physics and collisions', '/assets/materials/unity-physics.pdf'),
(35, 'UI Design Guide', 'Creating UIs in Unity', '/assets/materials/unity-ui-guide.pdf'),
(36, 'Audio Integration', 'Adding audio to Unity games', '/assets/materials/unity-audio.pdf'),
(37, 'Animation Guide', 'Animating characters in Unity', '/assets/materials/unity-animation.pdf'),
(38, 'AI Basics', 'Implementing AI in Unity', '/assets/materials/unity-ai.pdf'),
(39, 'Optimization Guide', 'Optimizing Unity games', '/assets/materials/unity-optimization.pdf'),
(40, 'Publishing Guide', 'Steps to publish Unity games', '/assets/materials/unity-publishing.pdf'),
-- UI/UX Design Principles (CourseID 5)
(41, 'Design Thinking Overview', 'Principles of design thinking', '/assets/materials/design-thinking.pdf'),
(42, 'User Research Guide', 'Conducting user research', '/assets/materials/user-research.pdf'),
(43, 'Wireframing Tools', 'Overview of wireframing tools', '/assets/materials/wireframing-tools.pdf'),
(44, 'Color Theory Guide', 'Understanding color theory', '/assets/materials/color-theory.pdf'),
(45, 'Typography Guide', 'Choosing typography for UI', '/assets/materials/typography-guide.pdf'),
(46, 'User Personas Template', 'Template for creating personas', '/assets/materials/user-personas.pdf'),
(47, 'Usability Testing Guide', 'Conducting usability tests', '/assets/materials/usability-testing.pdf'),
(48, 'Mobile-First Design', 'Designing for mobile first', '/assets/materials/mobile-first.pdf'),
-- Advanced JavaScript (CourseID 6, LessonIDs 49-57)
(49, 'Advanced JavaScript Guide', 'Explore essential advanced JavaScript concepts', '/assets/materials/js-advanced-guide.pdf'),
(50, 'Closures Guide', 'Detailed closures explanation', '/assets/materials/closures-guide.pdf'),
(51, 'Prototypes Reference', 'Prototype chain overview', '/assets/materials/prototypes-reference.pdf'),
(52, 'Async Programming Guide', 'Async/await best practices', '/assets/materials/async-guide.pdf'),
(53, 'ES6+ Cheat Sheet', 'Modern JS syntax reference', '/assets/materials/es6-cheat-sheet.pdf'),
(54, 'Functional JS Guide', 'Functional programming principles', '/assets/materials/functional-js.pdf'),
(55, 'Design Patterns Reference', 'Common JS patterns', '/assets/materials/js-patterns.pdf'),
(56, 'Jest Testing Guide', 'Unit testing with Jest', '/assets/materials/jest-guide.pdf'),
(57, 'JS Optimization Tips', 'Performance optimization guide', '/assets/materials/js-optimization.pdf'),
(58, 'JS Project Specs', 'Complex JS project requirements', '/assets/materials/js-project-specs.pdf'),
-- Machine Learning Fundamentals (CourseID 7, LessonIDs 58-67)
(59, 'ML Concepts Guide', 'Overview of ML principles', '/assets/materials/ml-concepts.pdf'),
(60, 'Supervised Learning Guide', 'Supervised algorithms reference', '/assets/materials/supervised-guide.pdf'),
(61, 'Unsupervised Learning Guide', 'Unsupervised methods reference', '/assets/materials/unsupervised-guide.pdf'),
(62, 'Feature Engineering Guide', 'Crafting ML features', '/assets/materials/feature-eng-guide.pdf'),
(63, 'Model Evaluation Guide', 'Evaluating ML models', '/assets/materials/model-eval-guide.pdf'),
(64, 'Neural Networks Guide', 'Neural network basics', '/assets/materials/neural-guide.pdf'),
(65, 'Deep Learning Guide', 'Deep learning foundations', '/assets/materials/deep-learning-guide.pdf'),
(66, 'ML Applications Guide', 'Real-world ML use cases', '/assets/materials/ml-applications.pdf'),
(67, 'AI Ethics Guide', 'Ethical considerations in AI', '/assets/materials/ai-ethics-guide.pdf'),
(68, 'ML Capstone Specs', 'ML project requirements', '/assets/materials/ml-capstone-specs.pdf'),
-- iOS App Development with Swift (CourseID 8, LessonIDs 68-77)
(69, 'Swift Cheat Sheet', 'Swift syntax reference', '/assets/materials/swift-cheat-sheet.pdf'),
(70, 'iOS Architecture Guide', 'MVC and MVVM patterns', '/assets/materials/ios-arch-guide.pdf'),
(71, 'UIKit Reference', 'UIKit components overview', '/assets/materials/uikit-reference.pdf'),
(72, 'SwiftUI Reference', 'SwiftUI components overview', '/assets/materials/swiftui-reference.pdf'),
(73, 'Data Handling Guide', 'Managing data in iOS', '/assets/materials/ios-data-guide.pdf'),
(74, 'API Integration Guide', 'Connecting to APIs', '/assets/materials/ios-api-guide.pdf'),
(75, 'Core Data Guide', 'Using Core Data', '/assets/materials/core-data-guide.pdf'),
(76, 'iOS Testing Guide', 'Testing iOS apps', '/assets/materials/ios-testing-guide.pdf'),
(77, 'App Store Guide', 'App Store submission guide', '/assets/materials/app-store-guide.pdf'),
(78, 'iOS Capstone Specs', 'iOS project requirements', '/assets/materials/ios-capstone-specs.pdf'),
-- SQL Database Mastery (CourseID 9, LessonIDs 78-86)
(79, 'DB Concepts Guide', 'Database principles overview', '/assets/materials/db-concepts-guide.pdf'),
(80, 'SQL CRUD Reference', 'Basic SQL operations', '/assets/materials/sql-crud-reference.pdf'),
(81, 'Advanced SQL Guide', 'Complex query techniques', '/assets/materials/sql-advanced-guide.pdf'),
(82, 'Normalization Guide', 'Database normalization', '/assets/materials/normalization-guide.pdf'),
(83, 'Indexing Guide', 'Optimizing with indexes', '/assets/materials/indexing-guide.pdf'),
(84, 'Stored Procedures Guide', 'Writing stored procedures', '/assets/materials/stored-proc-guide.pdf'),
(85, 'Transactions Guide', 'Managing transactions', '/assets/materials/transactions-guide.pdf'),
(86, 'DB Security Guide', 'Securing databases', '/assets/materials/db-security-guide.pdf'),
(87, 'DB Project Specs', 'Real-world DB project', '/assets/materials/db-project-specs.pdf'),
-- DevOps Engineering (CourseID 10, LessonIDs 87-96)
(88, 'DevOps Principles Guide', 'Core DevOps concepts', '/assets/materials/devops-principles.pdf'),
(89, 'Git Reference', 'Git commands and workflows', '/assets/materials/git-reference.pdf'),
(90, 'CI/CD Guide', 'Building CI/CD pipelines', '/assets/materials/cicd-guide.pdf'),
(91, 'Docker Reference', 'Containerization basics', '/assets/materials/docker-reference.pdf'),
(92, 'Kubernetes Guide', 'Container orchestration', '/assets/materials/k8s-guide.pdf'),
(93, 'Terraform Guide', 'Infrastructure as Code', '/assets/materials/terraform-guide.pdf'),
(94, 'Monitoring Guide', 'Monitoring and logging', '/assets/materials/monitoring-guide.pdf'),
(95, 'Cloud Services Guide', 'Using cloud platforms', '/assets/materials/cloud-guide.pdf'),
(96, 'DevOps Practices Guide', 'Best practices for DevOps', '/assets/materials/devops-practices.pdf'),
(97, 'DevOps Capstone Specs', 'DevOps project requirements', '/assets/materials/devops-capstone.pdf'),
-- Ethical Hacking (CourseID 11, LessonIDs 97-108)
(98, 'Hacking Principles Guide', 'Ethical hacking basics', '/assets/materials/hacking-principles.pdf'),
(99, 'Recon Guide', 'Information gathering techniques', '/assets/materials/recon-guide.pdf'),
(100, 'Scanning Guide', 'Network scanning tools', '/assets/materials/scanning-guide.pdf'),
(101, 'Enumeration Guide', 'System enumeration methods', '/assets/materials/enumeration-guide.pdf'),
(102, 'Vuln Analysis Guide', 'Vulnerability scanning', '/assets/materials/vuln-analysis.pdf'),
(103, 'Exploitation Guide', 'System exploitation techniques', '/assets/materials/exploitation-guide.pdf'),
(104, 'Malware Guide', 'Understanding malware', '/assets/materials/malware-guide.pdf'),
(105, 'Sniffing Guide', 'Packet sniffing techniques', '/assets/materials/sniffing-guide.pdf'),
(106, 'Social Eng Guide', 'Social engineering tactics', '/assets/materials/social-eng-guide.pdf'),
(107, 'DoS Guide', 'Denial-of-service attacks', '/assets/materials/dos-guide.pdf'),
(108, 'Session Hijack Guide', 'Session hijacking methods', '/assets/materials/session-hijack.pdf'),
(109, 'Web Security Guide', 'Securing web apps', '/assets/materials/web-security-guide.pdf'),
-- Deep Learning with TensorFlow (CourseID 12, LessonIDs 109-118)
(110, 'DL Concepts Guide', 'Deep learning principles', '/assets/materials/dl-concepts.pdf'),
(111, 'TensorFlow Reference', 'TensorFlow basics', '/assets/materials/tf-reference.pdf'),
(112, 'NN Design Guide', 'Designing neural networks', '/assets/materials/nn-design.pdf'),
(113, 'CNN Guide', 'Convolutional neural nets', '/assets/materials/cnn-guide.pdf'),
(114, 'RNN Guide', 'Recurrent neural nets', '/assets/materials/rnn-guide.pdf'),
(115, 'Transfer Learning Guide', 'Using pre-trained models', '/assets/materials/transfer-learning.pdf'),
(116, 'Generative Models Guide', 'Generative AI models', '/assets/materials/generative-models.pdf'),
(117, 'NLP Guide', 'Natural language processing', '/assets/materials/nlp-guide.pdf'),
(118, 'Vision Guide', 'Computer vision applications', '/assets/materials/vision-guide.pdf'),
(119, 'DL Deployment Guide', 'Deploying DL models', '/assets/materials/dl-deployment.pdf'),
-- Blockchain Development (CourseID 13, LessonIDs 119-128)
(120, 'Blockchain Guide', 'Blockchain technology overview', '/assets/materials/blockchain-guide.pdf'),
(121, 'Crypto Guide', 'Cryptographic principles', '/assets/materials/crypto-guide.pdf'),
(122, 'Bitcoin Guide', 'Bitcoin protocol details', '/assets/materials/bitcoin-guide.pdf'),
(123, 'Ethereum Guide', 'Smart contracts on Ethereum', '/assets/materials/ethereum-guide.pdf'),
(124, 'Solidity Reference', 'Solidity programming guide', '/assets/materials/solidity-reference.pdf'),
(125, 'Building DApps', 'Creating decentralized apps', '/assets/materials/dapp-guide.pdf'),
(126, 'Web3 Guide', 'Frontend-blockchain integration', '/assets/materials/web3-guide.pdf'),
(127, 'Testing Smart Contracts', 'Testing smart contracts', '/assets/materials/contract-testing.pdf'),
(128, 'Blockchain Security', 'Securing blockchain apps', '/assets/materials/blockchain-security.pdf'),
(129, 'Blockchain Capstone Specs', 'Blockchain project requirements', '/assets/materials/blockchain-capstone.pdf'),
-- Internet of Things Fundamentals (CourseID 14, LessonIDs 129-138)
(130, 'IoT Principles Guide', 'Core IoT concepts', '/assets/materials/iot-principles.pdf'),
(131, 'Sensors Guide', 'Working with sensors', '/assets/materials/sensors-guide.pdf'),
(132, 'Arduino Guide', 'Programming Arduino', '/assets/materials/arduino-guide.pdf'),
(133, 'Raspberry Pi Guide', 'Setting up Raspberry Pi', '/assets/materials/raspberry-guide.pdf'),
(134, 'IoT Protocols Guide', 'Networking protocols', '/assets/materials/iot-protocols.pdf'),
(135, 'IoT Cloud Guide', 'Cloud platforms for IoT', '/assets/materials/iot-cloud.pdf'),
(136, 'IoT Security Guide', 'Securing IoT devices', '/assets/materials/iot-security.pdf'),
(137, 'IoT Analytics Guide', 'Analyzing IoT data', '/assets/materials/iot-analytics.pdf'),
(138, 'IoT Solution Guide', 'End-to-end IoT project', '/assets/materials/iot-solution.pdf'),
(139, 'IoT Applications Guide', 'Real-world IoT use cases', '/assets/materials/iot-applications.pdf'),
-- Vue.js for Frontend Development (CourseID 15, LessonIDs 139-148)
(140, 'Vue Setup Guide', 'Getting started with Vue.js', '/assets/materials/vue-setup.pdf'),
(141, 'Vue Components Guide', 'Building components', '/assets/materials/vue-components.pdf'),
(142, 'Vue Directives Guide', 'Using Vue directives', '/assets/materials/vue-directives.pdf'),
(143, 'Vue Reactivity Guide', 'Reactivity system overview', '/assets/materials/vue-reactivity.pdf'),
(144, 'Vue Router Guide', 'Navigation with Vue Router', '/assets/materials/vue-router.pdf'),
(145, 'Vuex Guide', 'State management with Vuex', '/assets/materials/vuex-guide.pdf'),
(146, 'Vue Forms Guide', 'Handling forms in Vue', '/assets/materials/vue-forms.pdf'),
(147, 'Vue Practices Guide', 'Best practices for Vue', '/assets/materials/vue-practices.pdf'),
(148, 'Vue Testing Guide', 'Testing Vue apps', '/assets/materials/vue-testing.pdf'),
(149, 'Vue Capstone Specs', 'Vue project requirements', '/assets/materials/vue-capstone.pdf'),
-- Django Web Framework (CourseID 16, LessonIDs 149-158)
(150, 'Django Setup Guide', 'Installing Django', '/assets/materials/django-setup.pdf'),
(151, 'Django Models Guide', 'Creating models', '/assets/materials/django-models.pdf'),
(152, 'Django Views Guide', 'Building views and templates', '/assets/materials/django-views.pdf'),
(153, 'Django Forms Guide', 'Handling forms', '/assets/materials/django-forms.pdf'),
(154, 'Django Admin Guide', 'Customizing admin', '/assets/materials/django-admin.pdf'),
(155, 'Django Auth Guide', 'Implementing authentication', '/assets/materials/django-auth.pdf'),
(156, 'Django REST Guide', 'Building REST APIs', '/assets/materials/django-rest.pdf'),
(157, 'Django Testing Guide', 'Testing Django apps', '/assets/materials/django-testing.pdf'),
(158, 'Django Deployment Guide', 'Deploying Django', '/assets/materials/django-deployment.pdf'),
(159, 'Django Capstone Specs', 'Django project requirements', '/assets/materials/django-capstone.pdf');

-- Insert Quizzes
INSERT INTO Quizzes (LessonID, Title, Description, TimeLimit, PassingScore)
VALUES
-- Web Development Bootcamp (CourseID 1)
(1, 'HTML Basics Quiz', 'Test your knowledge of HTML fundamentals', 1800, 70),
(2, 'CSS Properties Quiz', 'Assess your understanding of CSS', 1800, 70),
(3, 'JavaScript Basics Quiz', 'Quiz on JavaScript fundamentals', 1800, 70),
(4, 'Website Building Quiz', 'Test your website creation skills', 1800, 70),
(5, 'Responsive Design Quiz', 'Test your responsive design knowledge', 1800, 70),
(6, 'React Components Quiz', 'Quiz on React basics', 1800, 70),
(7, 'React App Quiz', 'Assess your React app development', 1800, 70),
(8, 'Node.js Quiz', 'Test your Node.js knowledge', 1800, 70),
(9, 'REST API Quiz', 'Test your REST API knowledge', 1800, 70),
(10, 'Database Integration Quiz', 'Assess DB connectivity skills', 1800, 70),
(11, 'Deployment Quiz', 'Test your deployment knowledge', 1800, 70),
(12, 'Capstone Quiz', 'Final project knowledge test', 1800, 70),
-- Python for Data Science (CourseID 2, LessonIDs 13-20)
(13, 'Python Basics Quiz', 'Test your Python fundamentals', 1800, 70),
(14, 'Pandas Quiz', 'Assess Pandas data analysis skills', 1800, 70),
(15, 'Visualization Quiz', 'Test data visualization knowledge', 1800, 70),
(16, 'Statistics Quiz', 'Assess statistical analysis skills', 1800, 70),
(17, 'Scikit-learn Quiz', 'Test machine learning skills', 1800, 70),
(18, 'Data Cleaning Quiz', 'Assess data preprocessing skills', 1800, 70),
(19, 'API Quiz', 'Test API interaction knowledge', 1800, 70),
(20, 'Big Data Quiz', 'Assess big data processing skills', 1800, 70),
-- React Native Mobile Apps (CourseID 3, LessonIDs 21-30)
(21, 'React Native Setup Quiz', 'Test setup knowledge', 1800, 70),
(22, 'First App Quiz', 'Assess app building skills', 1800, 70),
(23, 'Navigation Quiz', 'Test navigation knowledge', 1800, 70),
(24, 'State Management Quiz', 'Assess state management skills', 1800, 70),
(25, 'Native Components Quiz', 'Test native feature usage', 1800, 70),
(26, 'Input Handling Quiz', 'Assess input handling skills', 1800, 70),
(27, 'Networking Quiz', 'Test networking knowledge', 1800, 70),
(28, 'Deployment Quiz', 'Assess app store deployment', 1800, 70),
(29, 'Performance Quiz', 'Test optimization knowledge', 1800, 70),
(30, 'React Native Capstone Quiz', 'Test project knowledge', 1800, 70),
-- Game Development with Unity (CourseID 4, LessonIDs 31-40)
(31, 'Unity Basics Quiz', 'Test Unity fundamentals', 1800, 70),
(32, 'Game Objects Quiz', 'Assess object handling skills', 1800, 70),
(33, 'C# Scripting Quiz', 'Test C# scripting knowledge', 1800, 70),
(34, 'Physics Quiz', 'Assess physics implementation', 1800, 70),
(35, 'Game UI Quiz', 'Test UI development skills', 1800, 70),
(36, 'Audio Quiz', 'Assess audio implementation', 1800, 70),
(37, 'Animation Quiz', 'Test animation skills', 1800, 70),
(38, 'Game AI Quiz', 'Assess AI implementation', 1800, 70),
(39, 'Optimization Quiz', 'Test performance optimization', 1800, 70),
(40, 'Publishing Quiz', 'Assess game publishing skills', 1800, 70),
-- UI/UX Design Principles (CourseID 5, LessonIDs 41-48)
(41, 'Design Principles Quiz', 'Test design fundamentals', 1800, 70),
(42, 'User Research Quiz', 'Assess research skills', 1800, 70),
(43, 'Wireframing Quiz', 'Test prototyping skills', 1800, 70),
(44, 'Color Theory Quiz', 'Assess color usage knowledge', 1800, 70),
(45, 'Typography Quiz', 'Test typography skills', 1800, 70),
(46, 'Personas Quiz', 'Assess persona creation', 1800, 70),
(47, 'Usability Testing Quiz', 'Test testing skills', 1800, 70),
(48, 'Mobile Design Quiz', 'Assess mobile-first design', 1800, 70),
-- Advanced JavaScript (CourseID 6, LessonIDs 49-57)
(49, 'Advanced JavaScript Quiz', 'Test your javascript knowledge', 1800, 70),
(50, 'Closures Quiz', 'Test your closures knowledge', 1800, 70),
(51, 'Prototypes Quiz', 'Assess prototype understanding', 1800, 70),
(52, 'Async Programming Quiz', 'Test async JS skills', 1800, 70),
(53, 'ES6+ Quiz', 'Assess modern JS features', 1800, 70),
(54, 'Functional JS Quiz', 'Test functional programming', 1800, 70),
(55, 'Design Patterns Quiz', 'Assess JS patterns', 1800, 70),
(56, 'Jest Testing Quiz', 'Test your testing skills', 1800, 70),
(57, 'JS Performance Quiz', 'Assess optimization skills', 1800, 70),
(58, 'JS Project Quiz', 'Test project knowledge', 1800, 70),
-- Machine Learning Fundamentals (CourseID 7, LessonIDs 58-67)
(59, 'ML Concepts Quiz', 'Test ML basics', 1800, 70),
(60, 'Supervised Learning Quiz', 'Assess supervised algorithms', 1800, 70),
(61, 'Unsupervised Learning Quiz', 'Test unsupervised methods', 1800, 70),
(62, 'Feature Engineering Quiz', 'Assess feature crafting', 1800, 70),
(63, 'Model Evaluation Quiz', 'Test evaluation techniques', 1800, 70),
(64, 'Neural Networks Quiz', 'Assess neural basics', 1800, 70),
(65, 'Deep Learning Quiz', 'Test deep learning basics', 1800, 70),
(66, 'ML Applications Quiz', 'Assess real-world ML', 1800, 70),
(67, 'AI Ethics Quiz', 'Test ethical knowledge', 1800, 70),
(68, 'ML Capstone Quiz', 'Test project knowledge', 1800, 70),
-- iOS App Development with Swift (CourseID 8, LessonIDs 68-77)
(69, 'Swift Basics Quiz', 'Test Swift fundamentals', 1800, 70),
(70, 'iOS Architecture Quiz', 'Assess architecture patterns', 1800, 70),
(71, 'UIKit Quiz', 'Test UIKit knowledge', 1800, 70),
(72, 'SwiftUI Quiz', 'Assess SwiftUI skills', 1800, 70),
(73, 'Data Handling Quiz', 'Test data management', 1800, 70),
(74, 'API Integration Quiz', 'Assess API skills', 1800, 70),
(75, 'Core Data Quiz', 'Test Core Data knowledge', 1800, 70),
(76, 'iOS Testing Quiz', 'Assess testing skills', 1800, 70),
(77, 'App Store Quiz', 'Test submission knowledge', 1800, 70),
(78, 'iOS Capstone Quiz', 'Test project knowledge', 1800, 70),
-- SQL Database Mastery (CourseID 9, LessonIDs 78-86)
(79, 'DB Concepts Quiz', 'Test database basics', 1800, 70),
(80, 'SQL CRUD Quiz', 'Assess CRUD operations', 1800, 70),
(81, 'Advanced SQL Quiz', 'Test complex queries', 1800, 70),
(82, 'Normalization Quiz', 'Assess normalization skills', 1800, 70),
(83, 'Indexing Quiz', 'Test indexing knowledge', 1800, 70),
(84, 'Stored Procedures Quiz', 'Assess stored procedures', 1800, 70),
(85, 'Transactions Quiz', 'Test transaction management', 1800, 70),
(86, 'DB Security Quiz', 'Assess security practices', 1800, 70),
(87, 'DB Project Quiz', 'Test project knowledge', 1800, 70),
-- DevOps Engineering (CourseID 10, LessonIDs 87-96)
(88, 'DevOps Principles Quiz', 'Test DevOps basics', 1800, 70),
(89, 'Git Quiz', 'Assess Git skills', 1800, 70),
(90, 'CI/CD Quiz', 'Test CI/CD knowledge', 1800, 70),
(91, 'Docker Quiz', 'Assess Docker skills', 1800, 70),
(92, 'Kubernetes Quiz', 'Test Kubernetes knowledge', 1800, 70),
(93, 'IaC Quiz', 'Assess Terraform skills', 1800, 70),
(94, 'Monitoring Quiz', 'Test monitoring skills', 1800, 70),
(95, 'Cloud Services Quiz', 'Assess cloud knowledge', 1800, 70),
(96, 'DevOps Practices Quiz', 'Test best practices', 1800, 70),
(97, 'DevOps Capstone Quiz', 'Test project knowledge', 1800, 70),
-- Ethical Hacking (CourseID 11, LessonIDs 97-108)
(98, 'Hacking Basics Quiz', 'Test hacking fundamentals', 1800, 70),
(99, 'Recon Quiz', 'Assess reconnaissance skills', 1800, 70),
(100, 'Scanning Quiz', 'Test scanning knowledge', 1800, 70),
(101, 'Enumeration Quiz', 'Assess enumeration skills', 1800, 70),
(102, 'Vuln Analysis Quiz', 'Test vulnerability skills', 1800, 70),
(103, 'Exploitation Quiz', 'Assess exploitation skills', 1800, 70),
(104, 'Malware Quiz', 'Test malware knowledge', 1800, 70),
(105, 'Sniffing Quiz', 'Assess sniffing skills', 1800, 70),
(106, 'Social Eng Quiz', 'Test social engineering', 1800, 70),
(107, 'DoS Quiz', 'Assess DoS knowledge', 1800, 70),
(108, 'Session Hijack Quiz', 'Test session hijacking', 1800, 70),
(109, 'Web Security Quiz', 'Assess web security', 1800, 70),
-- Deep Learning with TensorFlow (CourseID 12, QuizIDs for LessonIDs 109-118)
(110, 'DL Concepts Quiz', 'Test deep learning basics', 1800, 70),
(111, 'TensorFlow Quiz', 'Assess TensorFlow skills', 1800, 70),
(112, 'Neural Networks Quiz', 'Test NN design', 1800, 70),
(113, 'CNN Quiz', 'Assess CNN knowledge', 1800, 70),
(114, 'RNN Quiz', 'Test RNN knowledge', 1800, 70),
(115, 'Transfer Learning Quiz', 'Assess transfer learning', 1800, 70),
(116, 'Generative Models Quiz', 'Test generative AI', 1800, 70),
(117, 'NLP Quiz', 'Assess NLP skills', 1800, 70),
(118, 'Vision Quiz', 'Test computer vision', 1800, 70),
(119, 'DL Deployment Quiz', 'Assess deployment skills', 1800, 70),
-- Blockchain Development (CourseID 13, QuizIDs for LessonIDs 119-128)
(120, 'Blockchain Basics Quiz', 'Test blockchain fundamentals', 1800, 70),
(121, 'Crypto Quiz', 'Assess crypto knowledge', 1800, 70),
(122, 'Bitcoin Quiz', 'Test Bitcoin knowledge', 1800, 70),
(123, 'Ethereum Quiz', 'Assess Ethereum skills', 1800, 70),
(124, 'Solidity Quiz', 'Test Solidity knowledge', 1800, 70),
(125, 'DApp Quiz', 'Assess DApp skills', 1800, 70),
(126, 'Web3 Quiz', 'Test Web3 integration', 1800, 70),
(127, 'Contract Testing Quiz', 'Assess testing skills', 1800, 70),
(128, 'Blockchain Security Quiz', 'Test security practices', 1800, 70),
(129, 'Blockchain Capstone Quiz', 'Test project knowledge', 1800, 70),
-- Internet of Things Fundamentals (CourseID 14, LessonIDs 129-138)
(130, 'IoT Basics Quiz', 'Test IoT fundamentals', 1800, 70),
(131, 'Sensors Quiz', 'Assess sensor knowledge', 1800, 70),
(132, 'Arduino Quiz', 'Test Arduino skills', 1800, 70),
(133, 'Raspberry Pi Quiz', 'Assess Raspberry Pi skills', 1800, 70),
(134, 'IoT Protocols Quiz', 'Test protocol knowledge', 1800, 70),
(135, 'IoT Cloud Quiz', 'Assess cloud skills', 1800, 70),
(136, 'IoT Security Quiz', 'Test security knowledge', 1800, 70),
(137, 'IoT Analytics Quiz', 'Assess analytics skills', 1800, 70),
(138, 'IoT Solution Quiz', 'Test project knowledge', 1800, 70),
(139, 'IoT Applications Quiz', 'Assess application knowledge', 1800, 70),
-- Vue.js for Frontend Development (CourseID 15, LessonIDs 139-148)
(140, 'Vue Basics Quiz', 'Test Vue fundamentals', 1800, 70),
(141, 'Vue Components Quiz', 'Assess component skills', 1800, 70),
(142, 'Vue Directives Quiz', 'Test directive knowledge', 1800, 70),
(143, 'Vue Reactivity Quiz', 'Assess reactivity skills', 1800, 70),
(144, 'Vue Router Quiz', 'Test router knowledge', 1800, 70),
(145, 'Vuex Quiz', 'Assess Vuex skills', 1800, 70),
(146, 'Vue Forms Quiz', 'Test form handling', 1800, 70),
(147, 'Vue Practices Quiz', 'Assess best practices', 1800, 70),
(148, 'Vue Testing Quiz', 'Test testing skills', 1800, 70),
(149, 'Vue Capstone Quiz', 'Test project knowledge', 1800, 70),
-- Django Web Framework (CourseID 16, LessonIDs 149-158)
(150, 'Django Basics Quiz', 'Test Django fundamentals', 1800, 70),
(151, 'Django Models Quiz', 'Assess model skills', 1800, 70),
(152, 'Django Views Quiz', 'Test view knowledge', 1800, 70),
(153, 'Django Forms Quiz', 'Assess form skills', 1800, 70),
(154, 'Django Admin Quiz', 'Test admin customization', 1800, 70),
(155, 'Django Auth Quiz', 'Assess auth skills', 1800, 70),
(156, 'Django REST Quiz', 'Test REST API skills', 1800, 70),
(157, 'Django Testing Quiz', 'Assess testing skills', 1800, 70),
(158, 'Django Deployment Quiz', 'Test deployment knowledge', 1800, 70),
(159, 'Django Capstone Quiz', 'Test project knowledge', 1800, 70);

-- Insert PaymentTransactions
INSERT INTO PaymentTransactions (OrderID, RefundRequestID, TransactionType, Provider, ProviderTransactionID, BankAccountInfo)
VALUES
(1, NULL, 'payment', 'PayPal', 'PAYID-MX123456', 'user@example.com'),
(2, NULL, 'payment', 'Stripe', 'ch_3K123456789', '****1234'),
(3, 2, 'refund', 'PayPal', 'REF-123456789', 'user@example.com'),
(4, NULL, 'payment', 'Stripe', 'ch_3K123456790', '****5678'),
(5, 3, 'refund', 'PayPal', 'REF-123456790', 'student3@example.com'),
(6, 4, 'refund', 'Stripe', 're_3K123456791', '****9012'),
(7, NULL, 'payment', 'PayPal', 'PAYID-MX123457', 'student5@example.com'),
(8, 5, 'refund', 'Stripe', 're_3K123456792', '****3456'),
(9, NULL, 'payment', 'PayPal', 'PAYID-MX123458', 'mike.smith@example.com'),
(10, NULL, 'payment', 'Stripe', 'ch_3K123456793', '****7890');

-- Insert LessonItems
INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID)
VALUES
-- Web Development Bootcamp (CourseID 1)
(1, 1, 'video', 1), (1, 2, 'material', 1), (1, 3, 'quiz', 1),
(2, 1, 'video', 2), (2, 2, 'material', 2), (2, 3, 'quiz', 2),
(3, 1, 'video', 3), (3, 2, 'material', 3), (3, 3, 'quiz', 3),
(4, 1, 'video', 4), (4, 2, 'material', 4), (4, 3, 'quiz', 4),
(5, 1, 'video', 5), (5, 2, 'material', 5), (5, 3, 'quiz', 5),
(6, 1, 'video', 6), (6, 2, 'material', 6), (6, 3, 'quiz', 6),
(7, 1, 'video', 7), (7, 2, 'material', 7), (7, 3, 'quiz', 7),
(8, 1, 'video', 8), (8, 2, 'material', 8), (8, 3, 'quiz', 8),
(9, 1, 'video', 9), (9, 2, 'material', 9), (9, 3, 'quiz', 9),
(10, 1, 'video', 10), (10, 2, 'material', 10), (10, 3, 'quiz',	10),
(11, 1, 'video', 11), (11, 2, 'material', 11), (11, 3, 'quiz', 11),
(12, 1, 'video', 12), (12, 2, 'material', 12), (12, 3, 'quiz', 12),

-- Python for Data Science (CourseID 2)
(13, 1, 'video', 13), (13, 2, 'material', 13), (13, 3, 'quiz', 13),
(14, 1, 'video', 14), (14, 2, 'material', 14), (14, 3, 'quiz', 14),
(15, 1, 'video', 15), (15, 2, 'material', 15), (15, 3, 'quiz', 15),
(16, 1, 'video', 16), (16, 2, 'material', 16), (16, 3, 'quiz', 16),
(17, 1, 'video', 17), (17, 2, 'material', 17), (17, 3, 'quiz', 17),
(18, 1, 'video', 18), (18, 2, 'material', 18), (18, 3, 'quiz', 18),
(19, 1, 'video', 19), (19, 2, 'material', 19), (19, 3, 'quiz', 19),
(20, 1, 'video', 20), (20, 2, 'material', 20), (20, 3, 'quiz', 20),

-- React Native Mobile Apps (CourseID 3)
(21, 1, 'video', 21), (21, 2, 'material', 21), (21, 3, 'quiz', 21),
(22, 1, 'video', 22), (22, 2, 'material', 22), (22, 3, 'quiz', 22),
(23, 1, 'video', 23), (23, 2, 'material', 23), (23, 3, 'quiz', 23),
(24, 1, 'video', 24), (24, 2, 'material', 24), (24, 3, 'quiz', 24),
(25, 1, 'video', 25), (25, 2, 'material', 25), (25, 3, 'quiz', 25),
(26, 1, 'video', 26), (26, 2, 'material', 26), (26, 3, 'quiz', 26),
(27, 1, 'video', 27), (27, 2, 'material', 27), (27, 3, 'quiz', 27),
(28, 1, 'video', 28), (28, 2, 'material', 28), (28, 3, 'quiz', 28),
(29, 1, 'video', 29), (29, 2, 'material', 29), (29, 3, 'quiz', 29),
(30, 1, 'video', 30), (30, 2, 'material', 30), (30, 3, 'quiz', 30),

-- Game Development with Unity (CourseID 4)
(31, 1, 'video', 31), (31, 2, 'material', 31), (31, 3, 'quiz', 31),
(32, 1, 'video', 32), (32, 2, 'material', 32), (32, 3, 'quiz', 32),
(33, 1, 'video', 33), (33, 2, 'material', 33), (33, 3, 'quiz', 33),
(34, 1, 'video', 34), (34, 2, 'material', 34), (34, 3, 'quiz', 34),
(35, 1, 'video', 35), (35, 2, 'material', 35), (35, 3, 'quiz', 35),
(36, 1, 'video', 36), (36, 2, 'material', 36), (36, 3, 'quiz', 36),
(37, 1, 'video', 37), (37, 2, 'material', 37), (37, 3, 'quiz', 37),
(38, 1, 'video', 38), (38, 2, 'material', 38), (38, 3, 'quiz', 38),
(39, 1, 'video', 39), (39, 2, 'material', 39), (39, 3, 'quiz', 39),
(40, 1, 'video', 40), (40, 2, 'material', 40), (40, 3, 'quiz', 40),

-- UI/UX Design Principles (CourseID 5)
(41, 1, 'video', 41), (41, 2, 'material', 41), (41, 3, 'quiz', 41),
(42, 1, 'video', 42), (42, 2, 'material', 42), (42, 3, 'quiz', 42),
(43, 1, 'video', 43), (43, 2, 'material', 43), (43, 3, 'quiz', 43),
(44, 1, 'video', 44), (44, 2, 'material', 44), (44, 3, 'quiz', 44),
(45, 1, 'video', 45), (45, 2, 'material', 45), (45, 3, 'quiz', 45),
(46, 1, 'video', 46), (46, 2, 'material', 46), (46, 3, 'quiz', 46),
(47, 1, 'video', 47), (47, 2, 'material', 47), (47, 3, 'quiz', 47),
(48, 1, 'video', 48), (48, 2, 'material', 48), (48, 3, 'quiz', 48),

-- Advanced JavaScript (CourseID 6)
(49, 1, 'video', 49), (49, 2, 'material', 49), (49, 3, 'quiz', 49),
(50, 1, 'video', 50), (50, 2, 'material', 50), (50, 3, 'quiz', 50),
(51, 1, 'video', 51), (51, 2, 'material', 51), (51, 3, 'quiz', 51),
(52, 1, 'video', 52), (52, 2, 'material', 52), (52, 3, 'quiz', 52),
(53, 1, 'video', 53), (53, 2, 'material', 53), (53, 3, 'quiz', 53),
(54, 1, 'video', 54), (54, 2, 'material', 54), (54, 3, 'quiz', 54),
(55, 1, 'video', 55), (55, 2, 'material', 55), (55, 3, 'quiz', 55),
(56, 1, 'video', 56), (56, 2, 'material', 56), (56, 3, 'quiz', 56),
(57, 1, 'video', 57), (57, 2, 'material', 57), (57, 3, 'quiz', 57),
(58, 1, 'video', 58), (58, 2, 'material', 58), (58, 3, 'quiz', 58),

-- Machine Learning Fundamentals (CourseID 7)
(59, 1, 'video', 59), (59, 2, 'material', 59), (59, 3, 'quiz', 59),
(60, 1, 'video', 60), (60, 2, 'material', 60), (60, 3, 'quiz', 60),
(61, 1, 'video', 61), (61, 2, 'material', 61), (61, 3, 'quiz', 61),
(62, 1, 'video', 62), (62, 2, 'material', 62), (62, 3, 'quiz', 62),
(63, 1, 'video', 63), (63, 2, 'material', 63), (63, 3, 'quiz', 63),
(64, 1, 'video', 64), (64, 2, 'material', 64), (64, 3, 'quiz', 64),
(65, 1, 'video', 65), (65, 2, 'material', 65), (65, 3, 'quiz', 65),
(66, 1, 'video', 66), (66, 2, 'material', 66), (66, 3, 'quiz', 66),
(67, 1, 'video', 67), (67, 2, 'material', 67), (67, 3, 'quiz', 67),
(68, 1, 'video', 68), (68, 2, 'material', 68), (68, 3, 'quiz', 68),

-- iOS App Development with Swift (CourseID 8)
(69, 1, 'video', 69), (69, 2, 'material', 69), (69, 3, 'quiz', 69),
(70, 1, 'video', 70), (70, 2, 'material', 70), (70, 3, 'quiz', 70),
(71, 1, 'video', 71), (71, 2, 'material', 71), (71, 3, 'quiz', 71),
(72, 1, 'video', 72), (72, 2, 'material', 72), (72, 3, 'quiz', 72),
(73, 1, 'video', 73), (73, 2, 'material', 73), (73, 3, 'quiz', 73),
(74, 1, 'video', 74), (74, 2, 'material', 74), (74, 3, 'quiz', 74),
(75, 1, 'video', 75), (75, 2, 'material', 75), (75, 3, 'quiz', 75),
(76, 1, 'video', 76), (76, 2, 'material', 76), (76, 3, 'quiz', 76),
(77, 1, 'video', 77), (77, 2, 'material', 77), (77, 3, 'quiz', 77),
(78, 1, 'video', 78), (78, 2, 'material', 78), (78, 3, 'quiz', 78),

-- SQL Database Mastery (CourseID 9)
(79, 1, 'video', 79), (79, 2, 'material', 79), (79, 3, 'quiz', 79),
(80, 1, 'video', 80), (80, 2, 'material', 80), (80, 3, 'quiz', 80),
(81, 1, 'video', 81), (81, 2, 'material', 81), (81, 3, 'quiz', 81),
(82, 1, 'video', 82), (82, 2, 'material', 82), (82, 3, 'quiz', 82),
(83, 1, 'video', 83), (83, 2, 'material', 83), (83, 3, 'quiz', 83),
(84, 1, 'video', 84), (84, 2, 'material', 84), (84, 3, 'quiz', 84),
(85, 1, 'video', 85), (85, 2, 'material', 85), (85, 3, 'quiz', 85),
(86, 1, 'video', 86), (86, 2, 'material', 86), (86, 3, 'quiz', 86),
(87, 1, 'video', 87), (87, 2, 'material', 87), (87, 3, 'quiz', 87),

-- DevOps Engineering (CourseID 10)
(88, 1, 'video', 88), (88, 2, 'material', 88), (88, 3, 'quiz', 88),
(89, 1, 'video', 89), (89, 2, 'material', 89), (89, 3, 'quiz', 89),
(90, 1, 'video', 90), (90, 2, 'material', 90), (90, 3, 'quiz', 90),
(91, 1, 'video', 91), (91, 2, 'material', 91), (91, 3, 'quiz', 91),
(92, 1, 'video', 92), (92, 2, 'material', 92), (92, 3, 'quiz', 92),
(93, 1, 'video', 93), (93, 2, 'material', 93), (93, 3, 'quiz', 93),
(94, 1, 'video', 94), (94, 2, 'material', 94), (94, 3, 'quiz', 94),
(95, 1, 'video', 95), (95, 2, 'material', 95), (95, 3, 'quiz', 95),
(96, 1, 'video', 96), (96, 2, 'material', 96), (96, 3, 'quiz', 96),
(97, 1, 'video', 97), (97, 2, 'material', 97), (97, 3, 'quiz', 97),

-- Ethical Hacking (CourseID 11)
(98, 1, 'video', 98), (98, 2, 'material', 98), (98, 3, 'quiz', 98),
(99, 1, 'video', 99), (99, 2, 'material', 99), (99, 3, 'quiz', 99),
(100, 1, 'video', 100), (100, 2, 'material', 100), (100, 3, 'quiz', 100),
(101, 1, 'video', 101), (101, 2, 'material', 101), (101, 3, 'quiz', 101),
(102, 1, 'video', 102), (102, 2, 'material', 102), (102, 3, 'quiz', 102),
(103, 1, 'video', 103), (103, 2, 'material', 103), (103, 3, 'quiz', 103),
(104, 1, 'video', 104), (104, 2, 'material', 104), (104, 3, 'quiz', 104),
(105, 1, 'video', 105), (105, 2, 'material', 105), (105, 3, 'quiz', 105),
(106, 1, 'video', 106), (106, 2, 'material', 106), (106, 3, 'quiz', 106),
(107, 1, 'video', 107), (107, 2, 'material', 107), (107, 3, 'quiz', 107),
(108, 1, 'video', 108), (108, 2, 'material', 108), (108, 3, 'quiz', 108),
(109, 1, 'video', 109), (109, 2, 'material', 109), (109, 3, 'quiz', 109),

-- Deep Learning with TensorFlow (CourseID 12)
(110, 1, 'video', 110), (110, 2, 'material', 110), (110, 3, 'quiz', 110),
(111, 1, 'video', 111), (111, 2, 'material', 111), (111, 3, 'quiz', 111),
(112, 1, 'video', 112), (112, 2, 'material', 112), (112, 3, 'quiz', 112),
(113, 1, 'video', 113), (113, 2, 'material', 113), (113, 3, 'quiz', 113),
(114, 1, 'video', 114), (114, 2, 'material', 114), (114, 3, 'quiz', 114),
(115, 1, 'video', 115), (115, 2, 'material', 115), (115, 3, 'quiz', 115),
(116, 1, 'video', 116), (116, 2, 'material', 116), (116, 3, 'quiz', 116),
(117, 1, 'video', 117), (117, 2, 'material', 117), (117, 3, 'quiz', 117),
(118, 1, 'video', 118), (118, 2, 'material', 118), (118, 3, 'quiz', 118),
(119, 1, 'video', 119), (119, 2, 'material', 119), (119, 3, 'quiz', 119),

-- Blockchain Development (CourseID 13)
(120, 1, 'video', 120), (120, 2, 'material', 120), (120, 3, 'quiz', 120),
(121, 1, 'video', 121), (121, 2, 'material', 121), (121, 3, 'quiz', 121),
(122, 1, 'video', 122), (122, 2, 'material', 122), (122, 3, 'quiz', 122),
(123, 1, 'video', 123), (123, 2, 'material', 123), (123, 3, 'quiz', 123),
(124, 1, 'video', 124), (124, 2, 'material', 124), (124, 3, 'quiz', 124),
(125, 1, 'video', 125), (125, 2, 'material', 125), (125, 3, 'quiz', 125),
(126, 1, 'video', 126), (126, 2, 'material', 126), (126, 3, 'quiz', 126),
(127, 1, 'video', 127), (127, 2, 'material', 127), (127, 3, 'quiz', 127),
(128, 1, 'video', 128), (128, 2, 'material', 128), (128, 3, 'quiz', 128),
(129, 1, 'video', 129), (129, 2, 'material', 129), (129, 3, 'quiz', 129),

-- Internet of Things Fundamentals (CourseID 14)
(130, 1, 'video', 130), (130, 2, 'material', 130), (130, 3, 'quiz', 130),
(131, 1, 'video', 131), (131, 2, 'material', 131), (131, 3, 'quiz', 131),
(132, 1, 'video', 132), (132, 2, 'material', 132), (132, 3, 'quiz', 132),
(133, 1, 'video', 133), (133, 2, 'material', 133), (133, 3, 'quiz', 133),
(134, 1, 'video', 134), (134, 2, 'material', 134), (134, 3, 'quiz', 134),
(135, 1, 'video', 135), (135, 2, 'material', 135), (135, 3, 'quiz', 135),
(136, 1, 'video', 136), (136, 2, 'material', 136), (136, 3, 'quiz', 136),
(137, 1, 'video', 137), (137, 2, 'material', 137), (137, 3, 'quiz', 137),
(138, 1, 'video', 138), (138, 2, 'material', 138), (138, 3, 'quiz', 138),
(139, 1, 'video', 139), (139, 2, 'material', 139), (139, 3, 'quiz', 139),

-- Vue.js for Frontend Development (CourseID 15)
(140, 1, 'video', 140), (140, 2, 'material', 140), (140, 3, 'quiz', 140),
(141, 1, 'video', 141), (141, 2, 'material', 141), (141, 3, 'quiz', 141),
(142, 1, 'video', 142), (142, 2, 'material', 142), (142, 3, 'quiz', 142),
(143, 1, 'video', 143), (143, 2, 'material', 143), (143, 3, 'quiz', 143),
(144, 1, 'video', 144), (144, 2, 'material', 144), (144, 3, 'quiz', 144),
(145, 1, 'video', 145), (145, 2, 'material', 145), (145, 3, 'quiz', 145),
(146, 1, 'video', 146), (146, 2, 'material', 146), (146, 3, 'quiz', 146),
(147, 1, 'video', 147), (147, 2, 'material', 147), (147, 3, 'quiz', 147),
(148, 1, 'video', 148), (148, 2, 'material', 148), (148, 3, 'quiz', 148),
(149, 1, 'video', 149), (149, 2, 'material', 149), (149, 3, 'quiz', 149),

-- Django Web Framework (CourseID 16)
(150, 1, 'video', 150), (150, 2, 'material', 150), (150, 3, 'quiz', 150),
(151, 1, 'video', 151), (151, 2, 'material', 151), (151, 3, 'quiz', 151),
(152, 1, 'video', 152), (152, 2, 'material', 152), (152, 3, 'quiz', 152),
(153, 1, 'video', 153), (153, 2, 'material', 153), (153, 3, 'quiz', 153),
(154, 1, 'video', 154), (154, 2, 'material', 154), (154, 3, 'quiz', 154),
(155, 1, 'video', 155), (155, 2, 'material', 155), (155, 3, 'quiz', 155),
(156, 1, 'video', 156), (156, 2, 'material', 156), (156, 3, 'quiz', 156),
(157, 1, 'video', 157), (157, 2, 'material', 157), (157, 3, 'quiz', 157),
(158, 1, 'video', 158), (158, 2, 'material', 158), (158, 3, 'quiz', 158),
(159, 1, 'video', 159), (159, 2, 'material', 159), (159, 3, 'quiz', 159);

-- Insert Questions
INSERT INTO Questions (QuizID, Content, Type, Points, OrderIndex) 
VALUES
-- Web Development Bootcamp (CourseID 1, QuizIDs for LessonIDs 1-12)
(1, 'What is HTML?', 'multiple_choice', 1, 1),
(1, 'What is a tag?', 'multiple_choice', 1, 2),
(2, 'What is CSS?', 'multiple_choice', 1, 1),
(2, 'What is a selector?', 'multiple_choice', 1, 2),
(3, 'What is JavaScript?', 'multiple_choice', 1, 1),
(3, 'What is a variable?', 'multiple_choice', 1, 2),
(4, 'What is a webpage?', 'multiple_choice', 1, 1),
(4, 'What is a layout?', 'multiple_choice', 1, 2),
(5, 'What is responsive design?', 'multiple_choice', 1, 1),
(5, 'What is a media query?', 'multiple_choice', 1, 2),
(6, 'What is React?', 'multiple_choice', 1, 1),
(6, 'What is a component?', 'multiple_choice', 1, 2),
(7, 'What is a React app?', 'multiple_choice', 1, 1),
(7, 'What is state?', 'multiple_choice', 1, 2),
(8, 'What is Node.js?', 'multiple_choice', 1, 1),
(8, 'What is npm?', 'multiple_choice', 1, 2),
(9, 'What is a REST API?', 'multiple_choice', 1, 1),
(9, 'What is JSON?', 'multiple_choice', 1, 2),
(10, 'What is a database?', 'multiple_choice', 1, 1),
(10, 'What is MongoDB?', 'multiple_choice', 1, 2),
(11, 'What is deployment?', 'multiple_choice', 1, 1),
(11, 'What is Heroku?', 'multiple_choice', 1, 2),
(12, 'What is a capstone project?', 'multiple_choice', 1, 1),
(12, 'What is a full-stack app?', 'multiple_choice', 1, 2),
-- Python for Data Science (CourseID 2, LessonIDs 13-20, QuizIDs 13-20, QuestionIDs 25-40)
(13, 'Which Python construct creates a new list using a concise syntax?', 'multiple_choice', 1, 1),
(13, 'What does the "def" keyword define in Python?', 'multiple_choice', 1, 2),
(14, 'How do you filter rows in a pandas DataFrame based on a column value?', 'multiple_choice', 1, 1),
(14, 'Which pandas method combines two DataFrames based on a key?', 'multiple_choice', 1, 2),
(15, 'Which matplotlib function creates a scatter plot?', 'multiple_choice', 1, 1),
(15, 'What seaborn function generates a heatmap for correlation matrices?', 'multiple_choice', 1, 2),
(16, 'Which numpy function calculates the standard deviation of an array?', 'multiple_choice', 1, 1),
(16, 'How do you create a numpy array with evenly spaced values?', 'multiple_choice', 1, 2),
(17, 'Which scikit-learn class performs logistic regression?', 'multiple_choice', 1, 1),
(17, 'What does the train_test_split function do in scikit-learn?', 'multiple_choice', 1, 2),
(18, 'Which pandas method handles missing values by dropping them?', 'multiple_choice', 1, 1),
(18, 'What is the purpose of label encoding in data preprocessing?', 'multiple_choice', 1, 2),
(19, 'Which Python library fetches data from REST APIs?', 'multiple_choice', 1, 1),
(19, 'What does the json() method do in the requests library?', 'multiple_choice', 1, 2),
(20, 'Which library enables parallel computing for large datasets in Python?', 'multiple_choice', 1, 1),
(20, 'What is the purpose of the Dask DataFrame?', 'multiple_choice', 1, 2),
-- React Native Mobile Apps (CourseID 3, LessonIDs 21-30, QuizIDs 21-30, QuestionIDs 41-60)
(21, 'Which command creates a new React Native project?', 'multiple_choice', 1, 1),
(21, 'What is the role of the package.json file in React Native?', 'multiple_choice', 1, 2),
(22, 'Which React Native component renders a touchable element?', 'multiple_choice', 1, 1),
(22, 'How do you apply styles to a React Native component?', 'multiple_choice', 1, 2),
(23, 'Which library manages navigation in React Native apps?', 'multiple_choice', 1, 1),
(23, 'What does createStackNavigator do in React Navigation?', 'multiple_choice', 1, 2),
(24, 'Which hook manages state in a functional React Native component?', 'multiple_choice', 1, 1),
(24, 'What is the purpose of the useEffect hook in React Native?', 'multiple_choice', 1, 2),
(25, 'Which React Native API accesses the device''s geolocation?', 'multiple_choice', 1, 1),
(25, 'What does the PermissionsAndroid.request method do?', 'multiple_choice', 1, 2),
(26, 'Which component handles user text input in React Native?', 'multiple_choice', 1, 1),
(26, 'What prop of TextInput triggers on text change?', 'multiple_choice', 1, 2),
(27, 'Which function makes HTTP GET requests in React Native?', 'multiple_choice', 1, 1),
(27, 'What is the purpose of the async keyword in API calls?', 'multiple_choice', 1, 2),
(28, 'Which command generates an APK for Android in React Native?', 'multiple_choice', 1, 1),
(28, 'What is the purpose of the Xcode project in iOS deployment?', 'multiple_choice', 1, 2),
(29, 'Which technique reduces unnecessary renders in React Native?', 'multiple_choice', 1, 1),
(29, 'What does the useMemo hook optimize in React Native?', 'multiple_choice', 1, 2),
(30, 'Which tool debugs JavaScript code in React Native?', 'multiple_choice', 1, 1),
(30, 'What is the purpose of the React Native CLI in app development?', 'multiple_choice', 1, 2),
-- Game Development with Unity (CourseID 4, LessonIDs 31-40, QuizIDs 31-40, QuestionIDs 61-80)
(31, 'Which Unity window allows scene editing?', 'multiple_choice', 1, 1),
(31, 'What is the purpose of the Project window in Unity?', 'multiple_choice', 1, 2),
(32, 'What is the core building block of a Unity scene?', 'multiple_choice', 1, 1),
(32, 'Which Unity component defines an object''s position?', 'multiple_choice', 1, 2),
(33, 'Which method in MonoBehaviour runs once at startup?', 'multiple_choice', 1, 1),
(33, 'What is the purpose of the Update method in Unity?', 'multiple_choice', 1, 2),
(34, 'Which Unity component detects physical collisions?', 'multiple_choice', 1, 1),
(34, 'What does a Rigidbody component add to a GameObject?', 'multiple_choice', 1, 2),
(35, 'Which Unity component manages UI layouts?', 'multiple_choice', 1, 1),
(35, 'What is the purpose of the RectTransform in Unity UI?', 'multiple_choice', 1, 2),
(36, 'Which component plays audio clips in Unity?', 'multiple_choice', 1, 1),
(36, 'What is the purpose of the AudioListener component?', 'multiple_choice', 1, 2),
(37, 'Which Unity system controls character animations?', 'multiple_choice', 1, 1),
(37, 'What does an Animator Controller manage?', 'multiple_choice', 1, 2),
(38, 'Which Unity feature enables AI pathfinding?', 'multiple_choice', 1, 1),
(38, 'What is the role of a NavMeshAgent component?', 'multiple_choice', 1, 2),
(39, 'Which technique improves Unity game performance?', 'multiple_choice', 1, 1),
(39, 'What does batching reduce in Unity?', 'multiple_choice', 1, 2),
(40, 'Which Unity menu configures platform-specific builds?', 'multiple_choice', 1, 1),
(40, 'What is the purpose of the Unity Asset Store in publishing?', 'multiple_choice', 1, 2),
-- UI/UX Design Principles (CourseID 5, LessonIDs 41-48, QuizIDs 41-48, QuestionIDs 81-96)
(41, 'What is the primary focus of UI design?', 'multiple_choice', 1, 1),
(41, 'How does UX design differ from UI design?', 'multiple_choice', 1, 2),
(42, 'Which UX method defines user goals and pain points?', 'multiple_choice', 1, 1),
(42, 'What is the purpose of a user journey map?', 'multiple_choice', 1, 2),
(43, 'Which tool creates low-fidelity wireframes?', 'multiple_choice', 1, 1),
(43, 'What is the benefit of prototyping in UX design?', 'multiple_choice', 1, 2),
(44, 'Which color model is used for digital UI design?', 'multiple_choice', 1, 1),
(44, 'What is the purpose of a monochromatic color scheme?', 'multiple_choice', 1, 2),
(45, 'Which typography factor affects text readability?', 'multiple_choice', 1, 1),
(45, 'What is the benefit of consistent font usage?', 'multiple_choice', 1, 2),
(46, 'What is a user persona in UX design?', 'multiple_choice', 1, 1),
(46, 'How does empathy mapping aid UX design?', 'multiple_choice', 1, 2),
(47, 'What is the goal of usability testing in UX?', 'multiple_choice', 1, 1),
(47, 'Which method observes user interactions during testing?', 'multiple_choice', 1, 2),
(48, 'What is the principle of mobile-first design?', 'multiple_choice', 1, 1),
(48, 'Why is responsive design essential for mobile apps?', 'multiple_choice', 1, 2),
-- Advanced JavaScript (CourseID 6, LessonIDs 49-58, QuizIDs 49-58, QuestionIDs 97-116)
(49, 'What determines a variable''s scope in JavaScript?', 'multiple_choice', 1, 1),
(49, 'What is a common use of closures in event handlers?', 'multiple_choice', 1, 2),
(50, 'What is a closure in JavaScript?', 'multiple_choice', 1, 1),
(50, 'How does the "this" keyword behave in a closure?', 'multiple_choice', 1, 2),
(51, 'How does JavaScript implement inheritance?', 'multiple_choice', 1, 1),
(51, 'What does Object.create() do in JavaScript?', 'multiple_choice', 1, 2),
(52, 'What is the purpose of a Promise in JavaScript?', 'multiple_choice', 1, 1),
(52, 'How does async/await improve Promise handling?', 'multiple_choice', 1, 2),
(53, 'Which ES6 feature simplifies object destructuring?', 'multiple_choice', 1, 1),
(53, 'What is the purpose of the rest parameter in ES6?', 'multiple_choice', 1, 2),
(54, 'What is a key principle of functional programming?', 'multiple_choice', 1, 1),
(54, 'What does the map() method do in functional programming?', 'multiple_choice', 1, 2),
(55, 'Which design pattern encapsulates module logic?', 'multiple_choice', 1, 1),
(55, 'What is the role of the Singleton pattern?', 'multiple_choice', 1, 2),
(56, 'Which JavaScript framework is used for unit testing?', 'multiple_choice', 1, 1),
(56, 'What is the purpose of mocking in JavaScript tests?', 'multiple_choice', 1, 2),
(57, 'Which technique optimizes event handling in JavaScript?', 'multiple_choice', 1, 1),
(57, 'What is the benefit of debouncing in performance?', 'multiple_choice', 1, 2),
(58, 'What does Webpack do in a JavaScript project?', 'multiple_choice', 1, 1),
(58, 'How does tree shaking improve JavaScript builds?', 'multiple_choice', 1, 2),
-- Machine Learning Fundamentals (CourseID 7, LessonIDs 59-68, QuizIDs 59-68, QuestionIDs 117-136)
(59, 'What is a machine learning pipeline?', 'multiple_choice', 1, 1),
(59, 'Which tool automates ML workflows?', 'multiple_choice', 1, 2),
(60, 'What distinguishes supervised learning from unsupervised?', 'multiple_choice', 1, 1),
(60, 'Which algorithm predicts continuous values?', 'multiple_choice', 1, 2),
(61, 'What is a common unsupervised learning task?', 'multiple_choice', 1, 1),
(61, 'Which algorithm groups similar data points?', 'multiple_choice', 1, 2),
(62, 'What is the purpose of feature selection in ML?', 'multiple_choice', 1, 1),
(62, 'Why is normalization used in feature engineering?', 'multiple_choice', 1, 2),
(63, 'Which metric measures binary classification accuracy?', 'multiple_choice', 1, 1),
(63, 'What does a ROC curve evaluate in ML?', 'multiple_choice', 1, 2),
(64, 'What is the role of a hidden layer in a neural network?', 'multiple_choice', 1, 1),
(64, 'Which activation function prevents vanishing gradients?', 'multiple_choice', 1, 2),
(65, 'What distinguishes deep learning from traditional ML?', 'multiple_choice', 1, 1),
(65, 'Which TensorFlow function defines a neural network layer?', 'multiple_choice', 1, 2),
(66, 'What is a real-world application of ML?', 'multiple_choice', 1, 1),
(66, 'What is the benefit of transfer learning?', 'multiple_choice', 1, 2),
(67, 'What ethical issue arises in ML data collection?', 'multiple_choice', 1, 1),
(67, 'How does algorithmic bias affect ML models?', 'multiple_choice', 1, 2),
(68, 'What is the purpose of k-fold cross-validation?', 'multiple_choice', 1, 1),
(68, 'What does overfitting indicate in an ML model?', 'multiple_choice', 1, 2),

-- iOS App Development with Swift (CourseID 8, LessonIDs 69-78, QuizIDs 69-78, QuestionIDs 137-156)
(69, 'Which Swift feature ensures type safety for nil values?', 'multiple_choice', 1, 1),
(69, 'What is the purpose of guard statements in Swift?', 'multiple_choice', 1, 2),
(70, 'Which design pattern structures iOS app logic?', 'multiple_choice', 1, 1),
(70, 'What is the role of the Controller in MVC?', 'multiple_choice', 1, 2),
(71, 'Which framework builds traditional iOS UIs?', 'multiple_choice', 1, 1),
(71, 'What is a UITableView used for in UIKit?', 'multiple_choice', 1, 2),
(72, 'What is the benefit of SwiftUI''s declarative syntax?', 'multiple_choice', 1, 1),
(72, 'What does the @State property wrapper do in SwiftUI?', 'multiple_choice', 1, 2),
(73, 'Which iOS framework stores persistent data?', 'multiple_choice', 1, 1),
(73, 'What is the purpose of UserDefaults in iOS?', 'multiple_choice', 1, 2),
(74, 'Which class handles HTTP requests in iOS?', 'multiple_choice', 1, 1),
(74, 'What does the dataTask method do in URLSession?', 'multiple_choice', 1, 2),
(75, 'What is the role of an NSManagedObject in Core Data?', 'multiple_choice', 1, 1),
(75, 'What does a persistent container manage in Core Data?', 'multiple_choice', 1, 2),
(76, 'Which framework tests Swift code in iOS?', 'multiple_choice', 1, 1),
(76, 'What is the purpose of XCUITest in iOS?', 'multiple_choice', 1, 2),
(77, 'What is required to submit an app to the App Store?', 'multiple_choice', 1, 1),
(77, 'What does App Store Connect manage?', 'multiple_choice', 1, 2),
(78, 'Which Xcode feature signs an iOS app?', 'multiple_choice', 1, 1),
(78, 'What is the purpose of an iOS provisioning profile?', 'multiple_choice', 1, 2),
-- SQL Database Mastery (CourseID 9, LessonIDs 79-87, QuizIDs 79-87, QuestionIDs 157-174)
(79, 'What defines a relational database?', 'multiple_choice', 1, 1),
(79, 'What is the role of a primary key?', 'multiple_choice', 1, 2),
(80, 'Which SQL clause filters query results?', 'multiple_choice', 1, 1),
(80, 'What does the GROUP BY clause do in SQL?', 'multiple_choice', 1, 2),
(81, 'Which SQL JOIN returns only matched rows?', 'multiple_choice', 1, 1),
(81, 'What is the purpose of a LEFT JOIN?', 'multiple_choice', 1, 2),
(82, 'What is the goal of database normalization?', 'multiple_choice', 1, 1),
(82, 'What does the second normal form eliminate?', 'multiple_choice', 1, 2),
(83, 'Which SQL structure improves query performance?', 'multiple_choice', 1, 1),
(83, 'What is a non-clustered index?', 'multiple_choice', 1, 2),
(84, 'What is a stored procedure in SQL?', 'multiple_choice', 1, 1),
(84, 'What is the benefit of parameterized queries?', 'multiple_choice', 1, 2),
(85, 'What ensures data integrity in SQL transactions?', 'multiple_choice', 1, 1),
(85, 'What does the COMMIT command do in a transaction?', 'multiple_choice', 1, 2),
(86, 'What is a common SQL database security practice?', 'multiple_choice', 1, 1),
(86, 'How does SQL injection exploit vulnerabilities?', 'multiple_choice', 1, 2),
(87, 'What is the purpose of a foreign key constraint?', 'multiple_choice', 1, 1),
(87, 'Which SQL command modifies an existing table?', 'multiple_choice', 1, 2),
-- DevOps Engineering (CourseID 10, LessonIDs 88-97, QuizIDs 88-97, QuestionIDs 175-194)
(88, 'What is the core principle of DevOps?', 'multiple_choice', 1, 1),
(88, 'What does continuous integration achieve?', 'multiple_choice', 1, 2),
(89, 'Which command stages changes in Git?', 'multiple_choice', 1, 1),
(89, 'What is the purpose of a Git branch?', 'multiple_choice', 1, 2),
(90, 'Which tool automates CI/CD pipelines?', 'multiple_choice', 1, 1),
(90, 'What does a CI pipeline typically include?', 'multiple_choice', 1, 2),
(91, 'What is continuous deployment in DevOps?', 'multiple_choice', 1, 1),
(91, 'How does a deployment pipeline differ from CI?', 'multiple_choice', 1, 2),
(92, 'What does a Docker image contain?', 'multiple_choice', 1, 1),
(92, 'What is the role of a Dockerfile?', 'multiple_choice', 1, 2),
(93, 'What is the primary function of Kubernetes?', 'multiple_choice', 1, 1),
(93, 'What does a Kubernetes Deployment manage?', 'multiple_choice', 1, 2),
(94, 'What is Infrastructure as Code (IaC)?', 'multiple_choice', 1, 1),
(94, 'Which tool provisions infrastructure in IaC?', 'multiple_choice', 1, 2),
(95, 'Which tool monitors application performance?', 'multiple_choice', 1, 1),
(95, 'What is the purpose of log aggregation in DevOps?', 'multiple_choice', 1, 2),
(96, 'Which AWS service supports serverless computing?', 'multiple_choice', 1, 1),
(96, 'What is the benefit of cloud orchestration?', 'multiple_choice', 1, 2),
(97, 'What is a key DevOps practice for collaboration?', 'multiple_choice', 1, 1),
(97, 'Why is version control critical in DevOps?', 'multiple_choice', 1, 2),
-- Ethical Hacking (CourseID 11, LessonIDs 98-109, QuizIDs 98-109, QuestionIDs 195-218)
(98, 'What is the purpose of ethical hacking?', 'multiple_choice', 1, 1),
(98, 'What is a penetration test''s primary goal?', 'multiple_choice', 1, 2),
(99, 'Which reconnaissance technique gathers public data?', 'multiple_choice', 1, 1),
(99, 'What does OSINT stand for in ethical hacking?', 'multiple_choice', 1, 2),
(100, 'Which tool scans network ports?', 'multiple_choice', 1, 1),
(100, 'What is the purpose of a port scan?', 'multiple_choice', 1, 2),
(101, 'What does enumeration identify in a network?', 'multiple_choice', 1, 1),
(101, 'Which protocol is used in SMB enumeration?', 'multiple_choice', 1, 2),
(102, 'Which tool assesses system vulnerabilities?', 'multiple_choice', 1, 1),
(102, 'What is a vulnerability scanner''s output?', 'multiple_choice', 1, 2),
(103, 'What is privilege escalation in system hacking?', 'multiple_choice', 1, 1),
(103, 'Which technique exploits weak passwords?', 'multiple_choice', 1, 2),
(104, 'What is a keylogger in malware attacks?', 'multiple_choice', 1, 1),
(104, 'What does a Trojan disguise itself as?', 'multiple_choice', 1, 2),
(105, 'Which tool captures network packets?', 'multiple_choice', 1, 1),
(105, 'What is the purpose of packet sniffing?', 'multiple_choice', 1, 2),
(106, 'What is phishing in social engineering?', 'multiple_choice', 1, 1),
(106, 'What is the goal of pretexting?', 'multiple_choice', 1, 2),
(107, 'What is a DoS attack''s objective?', 'multiple_choice', 1, 1),
(107, 'What distinguishes a DDoS attack?', 'multiple_choice', 1, 2),
(108, 'What is session hijacking in web security?', 'multiple_choice', 1, 1),
(108, 'How are session cookies exploited?', 'multiple_choice', 1, 2),
(109, 'What is a stored XSS attack?', 'multiple_choice', 1, 1),
(109, 'How does SQL injection manipulate a database?', 'multiple_choice', 1, 2),
-- Deep Learning with TensorFlow (CourseID 12, LessonIDs 110-119, QuizIDs 110-119, QuestionIDs 219-238)
(110, 'What is the core component of deep learning?', 'multiple_choice', 1, 1),
(110, 'What does backpropagation do in neural networks?', 'multiple_choice', 1, 2),
(111, 'Which TensorFlow class defines a neural network model?', 'multiple_choice', 1, 1),
(111, 'What is the purpose of Keras in TensorFlow?', 'multiple_choice', 1, 2),
(112, 'What is a dense layer in a neural network?', 'multiple_choice', 1, 1),
(112, 'What does the ReLU activation function do?', 'multiple_choice', 1, 2),
(113, 'What is the primary use of a CNN?', 'multiple_choice', 1, 1),
(113, 'What does a pooling layer reduce in a CNN?', 'multiple_choice', 1, 2),
(114, 'What is the strength of an RNN?', 'multiple_choice', 1, 1),
(114, 'What does an LSTM cell prevent in RNNs?', 'multiple_choice', 1, 2),
(115, 'What is transfer learning in deep learning?', 'multiple_choice', 1, 1),
(115, 'Which TensorFlow module supports pre-trained models?', 'multiple_choice', 1, 2),
(116, 'What are the two networks in a GAN?', 'multiple_choice', 1, 1),
(116, 'What is the role of the discriminator in a GAN?', 'multiple_choice', 1, 2),
(117, 'Which TensorFlow module processes text data?', 'multiple_choice', 1, 1),
(117, 'What is tokenization in NLP?', 'multiple_choice', 1, 2),
(118, 'What is the goal of object detection in computer vision?', 'multiple_choice', 1, 1),
(118, 'Which TensorFlow API supports image processing?', 'multiple_choice', 1, 2),
(119, 'What does TensorFlow Serving enable?', 'multiple_choice', 1, 1),
(119, 'What is model quantization in deployment?', 'multiple_choice', 1, 2),
-- Blockchain Development (CourseID 13, LessonIDs 120-129, QuizIDs 120-129, QuestionIDs 239-258)
(120, 'What ensures blockchain immutability?', 'multiple_choice', 1, 1),
(120, 'What is a block header in a blockchain?', 'multiple_choice', 1, 2),
(121, 'Which cryptographic function secures blockchain data?', 'multiple_choice', 1, 1),
(121, 'What is a private key in blockchain?', 'multiple_choice', 1, 2),
(122, 'What is the primary function of Bitcoin?', 'multiple_choice', 1, 1),
(122, 'What secures Bitcoin transactions?', 'multiple_choice', 1, 2),
(123, 'What does Ethereum enable beyond cryptocurrency?', 'multiple_choice', 1, 1),
(123, 'What is the gas fee in Ethereum?', 'multiple_choice', 1, 2),
(124, 'Which Solidity keyword defines a smart contract?', 'multiple_choice', 1, 1),
(124, 'What is the purpose of the payable modifier?', 'multiple_choice', 1, 2),
(125, 'What defines a decentralized application (DApp)?', 'multiple_choice', 1, 1),
(125, 'What does Web3.js interact with in DApps?', 'multiple_choice', 1, 2),
(126, 'Which tool tests smart contracts on Ethereum?', 'multiple_choice', 1, 1),
(126, 'What is a testnet in blockchain development?', 'multiple_choice', 1, 2),
(127, 'What is a reentrancy vulnerability in smart contracts?', 'multiple_choice', 1, 1),
(127, 'How does a gas limit prevent attacks?', 'multiple_choice', 1, 2),
(128, 'Which blockchain platform supports DApps?', 'multiple_choice', 1, 1),
(128, 'What is Truffle used for in blockchain?', 'multiple_choice', 1, 2),
(129, 'What does MetaMask provide in blockchain apps?', 'multiple_choice', 1, 1),
(129, 'What is the Ethereum Virtual Machine (EVM)?', 'multiple_choice', 1, 2),
-- Internet of Things Fundamentals (CourseID 14, LessonIDs 130-139, QuizIDs 130-139, QuestionIDs 259-278)
(130, 'What connects devices in an IoT system?', 'multiple_choice', 1, 1),
(130, 'Which protocol is lightweight for IoT devices?', 'multiple_choice', 1, 2),
(131, 'Which sensor measures temperature in IoT?', 'multiple_choice', 1, 1),
(131, 'What does an actuator do in an IoT system?', 'multiple_choice', 1, 2),
(132, 'What is the primary use of Arduino in IoT?', 'multiple_choice', 1, 1),
(132, 'What does the setup() function do in Arduino?', 'multiple_choice', 1, 2),
(133, 'What is the role of Raspberry Pi in IoT?', 'multiple_choice', 1, 1),
(133, 'What does a GPIO pin control on Raspberry Pi?', 'multiple_choice', 1, 2),
(134, 'What is the purpose of MQTT in IoT?', 'multiple_choice', 1, 1),
(134, 'What does the publish/subscribe model enable?', 'multiple_choice', 1, 2),
(135, 'Which AWS service manages IoT devices?', 'multiple_choice', 1, 1),
(135, 'What is the role of a cloud platform in IoT?', 'multiple_choice', 1, 2),
(136, 'What is a common IoT security threat?', 'multiple_choice', 1, 1),
(136, 'How does encryption protect IoT devices?', 'multiple_choice', 1, 2),
(137, 'What does edge computing achieve in IoT?', 'multiple_choice', 1, 1),
(137, 'Which tool processes IoT data streams?', 'multiple_choice', 1, 2),
(138, 'What integrates devices in an IoT system?', 'multiple_choice', 1, 1),
(138, 'What is the role of an IoT gateway?', 'multiple_choice', 1, 2),
(139, 'Which protocol is common in smart home devices?', 'multiple_choice', 1, 1),
(139, 'What is a smart home hub''s function?', 'multiple_choice', 1, 2),
-- Vue.js for Frontend Development (CourseID 15, LessonIDs 140-149, QuizIDs 140-149, QuestionIDs 279-298)
(140, 'What is the core feature of Vue.js?', 'multiple_choice', 1, 1),
(140, 'What does the data property do in a Vue instance?', 'multiple_choice', 1, 2),
(141, 'What is a Vue component''s structure?', 'multiple_choice', 1, 1),
(141, 'What is the purpose of props in Vue.js?', 'multiple_choice', 1, 2),
(142, 'Which Vue directive binds data to HTML attributes?', 'multiple_choice', 1, 1),
(142, 'What does the v-if directive control?', 'multiple_choice', 1, 2),
(143, 'What powers Vue.js''s reactivity system?', 'multiple_choice', 1, 1),
(143, 'What is a computed property''s benefit?', 'multiple_choice', 1, 2),
(144, 'What does Vue Router manage in a Vue app?', 'multiple_choice', 1, 1),
(144, 'What is a dynamic route in Vue Router?', 'multiple_choice', 1, 2),
(145, 'What is the purpose of Vuex in Vue.js?', 'multiple_choice', 1, 1),
(145, 'What does a Vuex mutation do?', 'multiple_choice', 1, 2),
(146, 'What does the v-model directive enable?', 'multiple_choice', 1, 1),
(146, 'How does a custom validator work in Vue.js forms?', 'multiple_choice', 1, 2),
(147, 'Which practice improves Vue.js code maintainability?', 'multiple_choice', 1, 1),
(147, 'What is the benefit of single-file components?', 'multiple_choice', 1, 2),
(148, 'Which tool tests Vue.js components?', 'multiple_choice', 1, 1),
(148, 'What does snapshot testing verify in Vue.js?', 'multiple_choice', 1, 2),
(149, 'What does the Vue CLI initialize?', 'multiple_choice', 1, 1),
(149, 'What is the purpose of a Vue.js production build?', 'multiple_choice', 1, 2),
-- Django Web Framework (CourseID 16, LessonIDs 150-159, QuizIDs 150-159, QuestionIDs 299-318)
(150, 'What is the primary function of Django?', 'multiple_choice', 1, 1),
(150, 'What does the settings.py file configure?', 'multiple_choice', 1, 2),
(151, 'What does a Django model represent?', 'multiple_choice', 1, 1),
(151, 'What is the purpose of a migration in Django?', 'multiple_choice', 1, 2),
(152, 'What does a Django view handle?', 'multiple_choice', 1, 1),
(152, 'What is the role of a Django template?', 'multiple_choice', 1, 2),
(153, 'What does a Django form validate?', 'multiple_choice', 1, 1),
(153, 'What is a ModelForm''s advantage?', 'multiple_choice', 1, 2),
(154, 'What is the Django admin interface used for?', 'multiple_choice', 1, 1),
(154, 'How do you create a Django superuser?', 'multiple_choice', 1, 2),
(155, 'Which Django module handles user authentication?', 'multiple_choice', 1, 1),
(155, 'What does the login_required decorator do?', 'multiple_choice', 1, 2),
(156, 'What does Django REST Framework build?', 'multiple_choice', 1, 1),
(156, 'What is a serializer''s role in Django REST?', 'multiple_choice', 1, 2),
(157, 'Which Django class tests views?', 'multiple_choice', 1, 1),
(157, 'What does the Django test client simulate?', 'multiple_choice', 1, 2),
(158, 'What does Gunicorn do in Django deployment?', 'multiple_choice', 1, 1),
(158, 'What is the role of Nginx in Django?', 'multiple_choice', 1, 2),
(159, 'What is a Django app''s structure?', 'multiple_choice', 1, 1),
(159, 'What does the manage.py file execute?', 'multiple_choice', 1, 2);

-- Insert Answers
INSERT INTO Answers (QuestionID, Content, IsCorrect, OrderIndex)
VALUES
-- Web Development Bootcamp (CourseID 1)
(1, 'HyperText Markup Language', 1, 1),
(1, 'HighText Machine Language', 0, 2),
(1, 'HyperTool Multi Language', 0, 3),
(1, 'HyperText Machine Language', 0, 4),
(2, 'HTML element enclosed in brackets', 1, 1),
(2, 'CSS property', 0, 2),
(2, 'JavaScript function', 0, 3),
(2, 'File type', 0, 4),
(3, 'Cascading Style Sheets', 1, 1),
(3, 'Computer Style System', 0, 2),
(3, 'Creative Style Solution', 0, 3),
(3, 'Coded Style Syntax', 0, 4),
(4, 'Pattern to target HTML elements', 1, 1),
(4, 'JavaScript function', 0, 2),
(4, 'CSS property', 0, 3),
(4, 'HTML attribute', 0, 4),
(5, 'Programming language for web', 1, 1),
(5, 'Markup language', 0, 2),
(5, 'Database language', 0, 3),
(5, 'Styling language', 0, 4),
(6, 'Data container in programming', 1, 1),
(6, 'HTML element', 0, 2),
(6, 'CSS property', 0, 3),
(6, 'Database field', 0, 4),
(7, 'A JavaScript application', 1, 1),
(7, 'A CSS framework', 0, 2),
(7, 'A database system', 0, 3),
(7, 'A server platform', 0, 4),
(8, 'HTML document displayed in browser', 1, 1),
(8, 'JavaScript function', 0, 2),
(8, 'CSS stylesheet', 0, 3),
(8, 'Server configuration', 0, 4),
(9, 'Arrangement of elements on page', 1, 1),
(9, 'JavaScript code', 0, 2),
(9, 'Database structure', 0, 3),
(9, 'Server configuration', 0, 4),
(10, 'Design adapts to screen size', 1, 1),
(10, 'Fixed-width design', 0, 2),
(10, 'Server-side rendering', 0, 3),
(10, 'Database optimization', 0, 4),
(11, 'JavaScript UI library', 1, 1),
(11, 'CSS framework', 0, 2),
(11, 'Backend framework', 0, 3),
(11, 'Database system', 0, 4),
(12, 'A reusable piece of UI', 1, 1),
(12, 'A JavaScript function', 0, 2),
(12, 'A CSS rule', 0, 3),
(12, 'A database query', 0, 4),
(13, 'Manages state in functional components', 1, 1), 
(13, 'Handles routing', 0, 2), 
(13, 'Fetches data', 0, 3), 
(13, 'Styles components', 0, 4),
(14, 'Component data storage', 1, 1),
(14, 'CSS property', 0, 2),
(14, 'HTML attribute', 0, 3),
(14, 'Database table', 0, 4),
(15, 'JavaScript runtime environment', 1, 1),
(15, 'Frontend framework', 0, 2),
(15, 'Database system', 0, 3),
(15, 'CSS library', 0, 4),
(16, 'Node package manager', 1, 1),
(16, 'New programming method', 0, 2),
(16, 'Network protocol module', 0, 3),
(16, 'Node processing middleware', 0, 4),
(17, 'Web API architectural style', 1, 1),
(17, 'Database query language', 0, 2),
(17, 'Frontend framework', 0, 3),
(17, 'Testing methodology', 0, 4),
(18, 'JavaScript Object Notation', 1, 1),
(18, 'Java Serialized Object Network', 0, 2),
(18, 'JavaScript Oriented Nodes', 0, 3),
(18, 'Java Standard Object Notation', 0, 4),
(19, 'Organized collection of data', 1, 1),
(19, 'Programming language', 0, 2),
(19, 'Web server', 0, 3),
(19, 'Frontend framework', 0, 4),
(20, 'NoSQL database', 1, 1),
(20, 'SQL database', 0, 2),
(20, 'Programming language', 0, 3),
(20, 'Web framework', 0, 4),
(21, 'Process of making app available', 1, 1),
(21, 'Writing application code', 0, 2),
(21, 'Testing application', 0, 3),
(21, 'Designing user interface', 0, 4),
(22, 'Cloud platform service', 1, 1),
(22, 'Database system', 0, 2),
(22, 'Frontend framework', 0, 3),
(22, 'Testing tool', 0, 4),
(23, 'Function with lexical scope', 1, 1), 
(23, 'Global variable', 0, 2), 
(23, 'Object method', 0, 3), 
(23, 'Class instance', 0, 4),
(24, 'App with frontend and backend', 1, 1),
(24, 'Mobile-only app', 0, 2),
(24, 'Frontend-only app', 0, 3),
(24, 'Database-only app', 0, 4),
-- Python for Data Science (CourseID 2, QuizIDs 13-20, QuestionIDs 25-40)
(25, 'List comprehension', 1, 1), (25, 'For loop', 0, 2), (25, 'While loop', 0, 3), (25, 'Lambda function', 0, 4),
(26, 'A function', 1, 1), (26, 'A class', 0, 2), (26, 'A variable', 0, 3), (26, 'A module', 0, 4),
(27, 'Using loc[] with a condition', 1, 1), (27, 'Using groupby()', 0, 2), (27, 'Using sort_values()', 0, 3), (27, 'Using pivot_table()', 0, 4),
(28, 'merge()', 1, 1), (28, 'concat()', 0, 2), (28, 'join()', 0, 3), (28, 'append()', 0, 4),
(29, 'scatter()', 1, 1), (29, 'plot()', 0, 2), (29, 'hist()', 0, 3), (29, 'bar()', 0, 4),
(30, 'heatmap()', 1, 1), (30, 'pairplot()', 0, 2), (30, 'boxplot()', 0, 3), (30, 'lineplot()', 0, 4),
(31, 'std()', 1, 1), (31, 'mean()', 0, 2), (31, 'var()', 0, 3), (31, 'sum()', 0, 4),
(32, 'arange()', 1, 1), (32, 'array()', 0, 2), (32, 'zeros()', 0, 3), (32, 'ones()', 0, 4),
(33, 'LogisticRegression', 1, 1), (33, 'LinearRegression', 0, 2), (33, 'KMeans', 0, 3), (33, 'DecisionTreeClassifier', 0, 4),
(34, 'Splits data for training/testing', 1, 1), (34, 'Fits the model', 0, 2), (34, 'Predicts outcomes', 0, 3), (34, 'Evaluates accuracy', 0, 4),
(35, 'dropna()', 1, 1), (35, 'fillna()', 0, 2), (35, 'replace()', 0, 3), (35, 'drop_duplicates()', 0, 4),
(36, 'Converts categorical to numeric', 1, 1), (36, 'Scales numerical data', 0, 2), (36, 'Removes outliers', 0, 3), (36, 'Joins datasets', 0, 4),
(37, 'requests', 1, 1), (37, 'urllib', 0, 2), (37, 'http', 0, 3), (37, 'json', 0, 4),
(38, 'Parses JSON response', 1, 1), (38, 'Sends POST request', 0, 2), (38, 'Encodes data', 0, 3), (38, 'Handles errors', 0, 4),
(39, 'Dask', 1, 1), (39, 'Pandas', 0, 2), (39, 'Numpy', 0, 3), (39, 'Scikit-learn', 0, 4),
(40, 'Handles large datasets', 1, 1), (40, 'Visualizes data', 0, 2), (40, 'Trains models', 0, 3), (40, 'Fetches APIs', 0, 4),
-- React Native Mobile Apps (CourseID 3, QuizIDs 21-30, QuestionIDs 41-60)
(41, 'npx react-native init', 1, 1), (41, 'npm create-react-app', 0, 2), (41, 'expo init', 0, 3), (41, 'yarn create native', 0, 4),
(42, 'Manages dependencies', 1, 1), (42, 'Styles components', 0, 2), (42, 'Handles navigation', 0, 3), (42, 'Builds the app', 0, 4),
(43, 'TouchableOpacity', 1, 1), (43, 'Text', 0, 2), (43, 'View', 0, 3), (43, 'Image', 0, 4),
(44, 'Using StyleSheet.create', 1, 1), (44, 'Using inline styles', 0, 2), (44, 'Using CSS files', 0, 3), (44, 'Using themes', 0, 4),
(45, 'React Navigation', 1, 1), (45, 'React Router', 0, 2), (45, 'Redux', 0, 3), (45, 'Axios', 0, 4),
(46, 'Creates a navigation stack', 1, 1), (46, 'Manages state', 0, 2), (46, 'Fetches data', 0, 3), (46, 'Styles screens', 0, 4),
(47, 'useState', 1, 1), (47, 'useEffect', 0, 2), (47, 'useContext', 0, 3), (47, 'useReducer', 0, 4),
(48, 'Handles side effects', 1, 1), (48, 'Manages state', 0, 2), (48, 'Fetches data', 0, 3), (48, 'Renders UI', 0, 4),
(49, 'Geolocation', 1, 1), (49, 'Camera', 0, 2), (49, 'Sensors', 0, 3), (49, 'Notifications', 0, 4),
(50, 'Requests runtime permissions', 1, 1), (50, 'Fetches location data', 0, 2), (50, 'Manages state', 0, 3), (50, 'Handles navigation', 0, 4),
(51, 'TextInput', 1, 1), (51, 'Text', 0, 2), (51, 'Button', 0, 3), (51, 'View', 0, 4),
(52, 'onChangeText', 1, 1), (52, 'onPress', 0, 2), (52, 'onSubmitEditing', 0, 3), (52, 'onFocus', 0, 4),
(53, 'fetch', 1, 1), (53, 'axios', 0, 2), (53, 'http', 0, 3), (53, 'xmlhttprequest', 0, 4),
(54, 'Enables asynchronous functions', 1, 1), (54, 'Handles errors', 0, 2), (54, 'Manages state', 0, 3), (54, 'Styles requests', 0, 4),
(55, 'react-native assembleRelease', 1, 1), (55, 'react-native run-android', 0, 2), (55, 'expo build:android', 0, 3), (55, 'npm build', 0, 4),
(56, 'Configures iOS build settings', 1, 1), (56, 'Runs the app', 0, 2), (56, 'Tests the app', 0, 3), (56, 'Deploys the app', 0, 4),
(57, 'Memoization', 1, 1), (57, 'State management', 0, 2), (57, 'Navigation', 0, 3), (57, 'API calls', 0, 4),
(58, 'Memoizes computed values', 1, 1), (58, 'Handles events', 0, 2), (58, 'Fetches data', 0, 3), (58, 'Styles components', 0, 4),
(59, 'React Native Debugger', 1, 1), (59, 'Xcode', 0, 2), (59, 'Android Studio', 0, 3), (59, 'VS Code', 0, 4),
(60, 'Manages project builds', 1, 1), (60, 'Styles components', 0, 2), (60, 'Handles navigation', 0, 3), (60, 'Fetches data', 0, 4),
-- Game Development with Unity (CourseID 4, QuizIDs 31-40, QuestionIDs 61-80)
(61, 'Scene View', 1, 1), (61, 'Game View', 0, 2), (61, 'Inspector', 0, 3), (61, 'Project Window', 0, 4),
(62, 'Manages assets', 1, 1), (62, 'Edits scenes', 0, 2), (62, 'Tests gameplay', 0, 3), (62, 'Configures builds', 0, 4),
(63, 'GameObject', 1, 1), (63, 'Component', 0, 2), (63, 'Script', 0, 3), (63, 'Asset', 0, 4),
(64, 'Transform', 1, 1), (64, 'Collider', 0, 2), (64, 'Rigidbody', 0, 3), (64, 'Mesh', 0, 4),
(65, 'Start', 1, 1), (65, 'Update', 0, 2), (65, 'FixedUpdate', 0, 3), (65, 'LateUpdate', 0, 4),
(66, 'Runs every frame', 1, 1), (66, 'Runs once', 0, 2), (66, 'Handles physics', 0, 3), (66, 'Initializes objects', 0, 4),
(67, 'Collider', 1, 1), (67, 'Rigidbody', 0, 2), (67, 'Transform', 0, 3), (67, 'MeshRenderer', 0, 4),
(68, 'Simulates physics', 1, 1), (68, 'Renders graphics', 0, 2), (68, 'Plays audio', 0, 3), (68, 'Handles input', 0, 4),
(69, 'Canvas', 1, 1), (69, 'RectTransform', 0, 2), (69, 'Text', 0, 3), (69, 'Button', 0, 4),
(70, 'Positions UI elements', 1, 1), (70, 'Renders graphics', 0, 2), (70, 'Handles input', 0, 3), (70, 'Plays audio', 0, 4),
(71, 'AudioSource', 1, 1), (71, 'AudioListener', 0, 2), (71, 'AudioMixer', 0, 3), (71, 'AudioClip', 0, 4),
(72, 'Captures audio input', 1, 1), (72, 'Plays audio', 0, 2), (72, 'Mixes audio', 0, 3), (72, 'Records audio', 0, 4),
(73, 'Animator', 1, 1), (73, 'Animation', 0, 2), (73, 'Rigidbody', 0, 3), (73, 'Transform', 0, 4),
(74, 'Controls animation states', 1, 1), (74, 'Plays animations', 0, 2), (74, 'Handles physics', 0, 3), (74, 'Renders graphics', 0, 4),
(75, 'NavMesh', 1, 1), (75, 'Collider', 0, 2), (75, 'Rigidbody', 0, 3), (75, 'Animator', 0, 4),
(76, 'Controls AI movement', 1, 1), (76, 'Blocks paths', 0, 2), (76, 'Renders paths', 0, 3), (76, 'Calculates paths', 0, 4),
(77, 'Occlusion culling', 1, 1), (77, 'Texture compression', 0, 2), (77, 'Polygon reduction', 0, 3), (77, 'Shader optimization', 0, 4),
(78, 'Reduces draw calls', 1, 1), (78, 'Increases textures', 0, 2), (78, 'Adds scripts', 0, 3), (78, 'Boosts framerate', 0, 4),
(79, 'Build Settings', 1, 1), (79, 'Project Settings', 0, 2), (79, 'Inspector', 0, 3), (79, 'Scene View', 0, 4),
(80, 'Provides assets', 1, 1), (80, 'Builds games', 0, 2), (80, 'Tests games', 0, 3), (80, 'Deploys games', 0, 4),
-- UI/UX Design Principles (CourseID 5, QuizIDs 41-48, QuestionIDs 81-96)
(81, 'Visual interface design', 1, 1), (81, 'User experience', 0, 2), (81, 'Backend logic', 0, 3), (81, 'Database management', 0, 4),
(82, 'Focuses on user journey', 1, 1), (82, 'Focuses on visuals', 0, 2), (82, 'Manages data', 0, 3), (82, 'Optimizes code', 0, 4),
(83, 'User personas', 1, 1), (83, 'Wireframes', 0, 2), (83, 'Prototypes', 0, 3), (83, 'Style guides', 0, 4),
(84, 'Visualizes user interactions', 1, 1), (84, 'Designs layouts', 0, 2), (84, 'Tests usability', 0, 3), (84, 'Analyzes data', 0, 4),
(85, 'Balsamiq', 1, 1), (85, 'Photoshop', 0, 2), (85, 'Illustrator', 0, 3), (85, 'Blender', 0, 4),
(86, 'Tests user interactions', 1, 1), (86, 'Creates layouts', 0, 2), (86, 'Analyzes data', 0, 3), (86, 'Optimizes code', 0, 4),
(87, 'RGB', 1, 1), (87, 'CMYK', 0, 2), (87, 'HSB', 0, 3), (87, 'Lab', 0, 4),
(88, 'Creates visual consistency', 1, 1), (88, 'Increases contrast', 0, 2), (88, 'Adds variety', 0, 3), (88, 'Enhances animations', 0, 4),
(89, 'Font size', 1, 1), (89, 'Color contrast', 0, 2), (89, 'Line spacing', 0, 3), (89, 'Text alignment', 0, 4),
(90, 'Improves readability', 1, 1), (90, 'Enhances performance', 0, 2), (90, 'Stores data', 0, 3), (90, 'Secures interfaces', 0, 4),
(91, 'Represents target users', 1, 1), (91, 'Designs layouts', 0, 2), (91, 'Tests usability', 0, 3), (91, 'Analyzes data', 0, 4),
(92, 'Understands user emotions', 1, 1), (92, 'Creates wireframes', 0, 2), (92, 'Tests prototypes', 0, 3), (92, 'Manages data', 0, 4),
(93, 'Improves usability', 1, 1), (93, 'Optimizes code', 0, 2), (93, 'Stores data', 0, 3), (93, 'Secures systems', 0, 4),
(94, 'Think-aloud protocol', 1, 1), (94, 'A/B testing', 0, 2), (94, 'Surveys', 0, 3), (94, 'Heatmaps', 0, 4),
(95, 'Design for small screens first', 1, 1), (95, 'Design for desktop first', 0, 2), (95, 'Ignore screen size', 0, 3), (95, 'Use fixed layouts', 0, 4),
(96, 'Adapts to all devices', 1, 1), (96, 'Uses static layouts', 0, 2), (96, 'Optimizes code', 0, 3), (96, 'Manages data', 0, 4),
-- Advanced JavaScript (CourseID 6, QuizIDs 49-58, QuestionIDs 97-116)
(97, 'Function retaining scope', 1, 1), (97, 'Global variable', 0, 2), (97, 'Class instance', 0, 3), (97, 'Module export', 0, 4),
(98, 'Refers to outer scope', 1, 1), (98, 'Refers to global object', 0, 2), (98, 'Refers to function', 0, 3), (98, 'Refers to module', 0, 4),
(99, 'Lexical environment', 1, 1), (99, 'Global scope', 0, 2), (99, 'Block scope', 0, 3), (99, 'Module scope', 0, 4),
(100, 'Maintains state in loops', 1, 1), (100, 'Styles components', 0, 2), (100, 'Fetches data', 0, 3), (100, 'Renders DOM', 0, 4),
(101, 'Prototypal inheritance', 1, 1), (101, 'Classical inheritance', 0, 2), (101, 'Module inheritance', 0, 3), (101, 'Functional inheritance', 0, 4),
(102, 'Creates a new object', 1, 1), (102, 'Clones an object', 0, 2), (102, 'Defines a class', 0, 3), (102, 'Merges objects', 0, 4),
(103, 'Handles async operations', 1, 1), (103, 'Manages state', 0, 2), (103, 'Renders DOM', 0, 3), (103, 'Parses JSON', 0, 4),
(104, 'Simplifies async code', 1, 1), (104, 'Handles errors', 0, 2), (104, 'Manages state', 0, 3), (104, 'Styles components', 0, 4),
(105, 'Destructuring assignment', 1, 1), (105, 'Arrow functions', 0, 2), (105, 'Template literals', 0, 3), (105, 'Default parameters', 0, 4),
(106, 'Collects multiple arguments', 1, 1), (106, 'Spreads arrays', 0, 2), (106, 'Parses JSON', 0, 3), (106, 'Handles events', 0, 4),
(107, 'Immutability', 1, 1), (107, 'State management', 0, 2), (107, 'DOM manipulation', 0, 3), (107, 'Event handling', 0, 4),
(108, 'Transforms array elements', 1, 1), (108, 'Filters arrays', 0, 2), (108, 'Reduces arrays', 0, 3), (108, 'Sorts arrays', 0, 4),
(109, 'Module pattern', 1, 1), (109, 'Singleton pattern', 0, 2), (109, 'Factory pattern', 0, 3), (109, 'Observer pattern', 0, 4),
(110, 'Ensures single instance', 1, 1), (110, 'Handles events', 0, 2), (110, 'Manages state', 0, 3), (110, 'Fetches data', 0, 4),
(111, 'Jest', 1, 1), (111, 'Mocha', 0, 2), (111, 'Chai', 0, 3), (111, 'Jasmine', 0, 4),
(112, 'Simulates dependencies', 1, 1), (112, 'Tests UI', 0, 2), (112, 'Fetches data', 0, 3), (112, 'Styles code', 0, 4),
(113, 'Debouncing', 1, 1), (113, 'Throttling', 0, 2), (113, 'Memoization', 0, 3), (113, 'Event delegation', 0, 4),
(114, 'Reduces rapid calls', 1, 1), (114, 'Increases calls', 0, 2), (114, 'Handles errors', 0, 3), (114, 'Parses data', 0, 4),
(115, 'Bundles modules', 1, 1), (115, 'Compiles code', 0, 2), (115, 'Tests code', 0, 3), (115, 'Formats code', 0, 4),
(116, 'Removes unused code', 1, 1), (116, 'Adds comments', 0, 2), (116, 'Minifies code', 0, 3), (116, 'Parses JSON', 0, 4),
-- Machine Learning Fundamentals (CourseID 7, QuizIDs 59-68, QuestionIDs 117-136)
(117, 'Uses labeled data', 1, 1), (117, 'Clusters data', 0, 2), (117, 'Reduces dimensions', 0, 3), (117, 'Optimizes code', 0, 4),
(118, 'Linear Regression', 1, 1), (118, 'Logistic Regression', 0, 2), (118, 'K-Means', 0, 3), (118, 'SVM', 0, 4),
(119, 'Clustering', 1, 1), (119, 'Classification', 0, 2), (119, 'Regression', 0, 3), (119, 'Feature extraction', 0, 4),
(120, 'K-Means', 1, 1), (120, 'Linear Regression', 0, 2), (120, 'Decision Tree', 0, 3), (120, 'SVM', 0, 4),
(121, 'Selects relevant features', 1, 1), (121, 'Trains models', 0, 2), (121, 'Tests models', 0, 3), (121, 'Visualizes data', 0, 4),
(122, 'Scales feature ranges', 1, 1), (122, 'Encodes categories', 0, 2), (122, 'Removes outliers', 0, 3), (122, 'Joins datasets', 0, 4),
(123, 'Precision', 1, 1), (123, 'Mean Squared Error', 0, 2), (123, 'R-Squared', 0, 3), (123, 'Silhouette Score', 0, 4),
(124, 'Measures model performance', 1, 1), (124, 'Plots data', 0, 2), (124, 'Stores data', 0, 3), (124, 'Encrypts data', 0, 4),
(125, 'Processes complex patterns', 1, 1), (125, 'Stores data', 0, 2), (125, 'Fetches data', 0, 3), (125, 'Renders UI', 0, 4),
(126, 'ReLU', 1, 1), (126, 'Sigmoid', 0, 2), (126, 'Tanh', 0, 3), (126, 'Softmax', 0, 4),
(127, 'Multiple layers', 1, 1), (127, 'Single layer', 0, 2), (127, 'No layers', 0, 3), (127, 'Linear models', 0, 4),
(128, 'Dense', 1, 1), (128, 'Conv2D', 0, 2), (128, 'LSTM', 0, 3), (128, 'Dropout', 0, 4),
(129, 'Fraud detection', 1, 1), (129, 'Data cleaning', 0, 2), (129, 'UI design', 0, 3), (129, 'API fetching', 0, 4),
(130, 'Reuses pre-trained weights', 1, 1), (130, 'Trains new models', 0, 2), (130, 'Cleans data', 0, 3), (130, 'Visualizes data', 0, 4),
(131, 'Privacy concerns', 1, 1), (131, 'Overfitting', 0, 2), (131, 'Underfitting', 0, 3), (131, 'Data drift', 0, 4),
(132, 'Skews predictions', 1, 1), (132, 'Improves accuracy', 0, 2), (132, 'Reduces cost', 0, 3), (132, 'Enhances visuals', 0, 4),
(133, 'Evaluates model robustness', 1, 1), (133, 'Trains models', 0, 2), (133, 'Cleans data', 0, 3), (133, 'Visualizes data', 0, 4),
(134, 'Model memorizes data', 1, 1), (134, 'Model underperforms', 0, 2), (134, 'Data is missing', 0, 3), (134, 'Code is slow', 0, 4),
(135, 'Automates data flow', 1, 1), (135, 'Trains models', 0, 2), (135, 'Tests code', 0, 3), (135, 'Deploys apps', 0, 4),
(136, 'MLflow', 1, 1), (136, 'TensorFlow', 0, 2), (136, 'Scikit-learn', 0, 3), (136, 'Pandas', 0, 4);
-- Continue inserting Answers for remaining courses (CourseIDs 8-16)
INSERT INTO Answers (QuestionID, Content, IsCorrect, OrderIndex)
VALUES
-- iOS App Development with Swift (CourseID 8, QuizIDs 69-78, QuestionIDs 137-156)
(137, 'Optionals', 1, 1), (137, 'Enums', 0, 2), (137, 'Structs', 0, 3), (137, 'Closures', 0, 4),
(138, 'Ensures early exits', 1, 1), (138, 'Handles errors', 0, 2), (138, 'Manages state', 0, 3), (138, 'Fetches data', 0, 4),
(139, 'MVC', 1, 1), (139, 'MVVM', 0, 2), (139, 'Singleton', 0, 3), (139, 'Factory', 0, 4),
(140, 'Handles user interactions', 1, 1), (140, 'Manages data', 0, 2), (140, 'Renders UI', 0, 3), (140, 'Fetches data', 0, 4),
(141, 'UIKit', 1, 1), (141, 'SwiftUI', 0, 2), (141, 'Core Data', 0, 3), (141, 'URLSession', 0, 4),
(142, 'Displays tabular data', 1, 1), (142, 'Renders images', 0, 2), (142, 'Handles input', 0, 3), (142, 'Plays audio', 0, 4),
(143, 'Simplifies UI code', 1, 1), (143, 'Handles networking', 0, 2), (143, 'Manages data', 0, 3), (143, 'Tests code', 0, 4),
(144, 'Manages reactive state', 1, 1), (144, 'Styles views', 0, 2), (144, 'Handles navigation', 0, 3), (144, 'Fetches data', 0, 4),
(145, 'Core Data', 1, 1), (145, 'UserDefaults', 0, 2), (145, 'Keychain', 0, 3), (145, 'FileManager', 0, 4),
(146, 'Stores small data', 1, 1), (146, 'Manages UI', 0, 2), (146, 'Fetches data', 0, 3), (146, 'Tests code', 0, 4),
(147, 'URLSession', 1, 1), (147, 'Alamofire', 0, 2), (147, 'Core Data', 0, 3), (147, 'SwiftUI', 0, 4),
(148, 'Fetches network data', 1, 1), (148, 'Stores data', 0, 2), (148, 'Renders UI', 0, 3), (148, 'Tests code', 0, 4),
(149, 'Represents data entities', 1, 1), (149, 'Renders UI', 0, 2), (149, 'Fetches data', 0, 3), (149, 'Handles input', 0, 4),
(150, 'Manages data storage', 1, 1), (150, 'Renders UI', 0, 2), (150, 'Fetches data', 0, 3), (150, 'Tests code', 0, 4),
(151, 'XCTest', 1, 1), (151, 'Quick', 0, 2), (151, 'Nimble', 0, 3), (151, 'Cuckoo', 0, 4),
(152, 'Tests UI interactions', 1, 1), (152, 'Tests data models', 0, 2), (152, 'Tests APIs', 0, 3), (152, 'Tests security', 0, 4),
(153, 'Apple Developer account', 1, 1), (153, 'AWS account', 0, 2), (153, 'GitHub account', 0, 3), (153, 'Google account', 0, 4),
(154, 'App submission process', 1, 1), (154, 'Build configuration', 0, 2), (154, 'Code testing', 0, 3), (154, 'Data storage', 0, 4),
(155, 'Code signing', 1, 1), (155, 'App building', 0, 2), (155, 'App testing', 0, 3), (155, 'App deployment', 0, 4),
(156, 'Authorizes app distribution', 1, 1), (156, 'Configures UI', 0, 2), (156, 'Manages data', 0, 3), (156, 'Tests code', 0, 4),
-- SQL Database Mastery (Course 9, QuestionIDs 157-158)
(157, 'Tables with related data', 1, 1), (157, 'Unstructured data storage', 0, 2), (157, 'Key-value pair storage', 0, 3), (157, 'Document-based storage', 0, 4),
(158, 'Uniquely identifies rows', 1, 1), (158, 'Joins tables', 0, 2), (158, 'Indexes columns', 0, 3), (158, 'Stores data types', 0, 4),
(159, 'WHERE', 1, 1), (159, 'HAVING', 0, 2), (159, 'GROUP BY', 0, 3), (159, 'ORDER BY', 0, 4),
(160, 'Groups rows by column values', 1, 1), (160, 'Filters individual rows', 0, 2), (160, 'Sorts query results', 0, 3), (160, 'Joins tables', 0, 4),
(161, 'INNER JOIN', 1, 1), (161, 'LEFT JOIN', 0, 2), (161, 'RIGHT JOIN', 0, 3), (161, 'FULL JOIN', 0, 4),
(162, 'Includes all left table rows', 1, 1), (162, 'Includes matched rows only', 0, 2), (162, 'Includes all right table rows', 0, 3), (162, 'Excludes unmatched rows', 0, 4),
(163, 'Reduces data redundancy', 1, 1), (163, 'Increases query speed', 0, 2), (163, 'Encrypts data', 0, 3), (163, 'Joins tables', 0, 4),
(164, 'Partial dependencies', 1, 1), (164, 'Transitive dependencies', 0, 2), (164, 'Functional dependencies', 0, 3), (164, 'Key constraints', 0, 4),
(165, 'Index', 1, 1), (165, 'View', 0, 2), (165, 'Trigger', 0, 3), (165, 'Constraint', 0, 4),
(166, 'Separate data structure', 1, 1), (166, 'Table copy', 0, 2), (166, 'Query cache', 0, 3), (166, 'Primary key', 0, 4),
(167, 'Reusable SQL logic', 1, 1), (167, 'Temporary table', 0, 2), (167, 'Query optimizer', 0, 3), (167, 'Data index', 0, 4),
(168, 'Prevents SQL injection', 1, 1), (168, 'Increases query speed', 0, 2), (168, 'Joins tables', 0, 3), (168, 'Stores data', 0, 4),
(169, 'ACID properties', 1, 1), (169, 'Data indexing', 0, 2), (169, 'Query optimization', 0, 3), (169, 'Table joins', 0, 4),
(170, 'Saves transaction changes', 1, 1), (170, 'Reverts changes', 0, 2), (170, 'Locks tables', 0, 3), (170, 'Joins data', 0, 4),
(171, 'Role-based access control', 1, 1), (171, 'Data normalization', 0, 2), (171, 'Query optimization', 0, 3), (171, 'Table indexing', 0, 4),
(172, 'Executes malicious queries', 1, 1), (172, 'Encrypts data', 0, 2), (172, 'Joins tables', 0, 3), (172, 'Caches queries', 0, 4),
(173, 'Enforces referential integrity', 1, 1), (173, 'Optimizes queries', 0, 2), (173, 'Encrypts data', 0, 3), (173, 'Groups rows', 0, 4),
(174, 'ALTER TABLE', 1, 1), (174, 'CREATE TABLE', 0, 2), (174, 'DROP TABLE', 0, 3), (174, 'UPDATE TABLE', 0, 4),
-- DevOps Engineering (CourseID 10, QuizIDs 88-97, QuestionIDs 175-194)
(175, 'Collaboration and automation', 1, 1), (175, 'Code optimization', 0, 2), (175, 'UI design', 0, 3), (175, 'Database management', 0, 4),
(176, 'Automates code integration', 1, 1), (176, 'Deploys applications', 0, 2), (176, 'Monitors servers', 0, 3), (176, 'Tests databases', 0, 4),
(177, 'git add', 1, 1), (177, 'git commit', 0, 2), (177, 'git push', 0, 3), (177, 'git pull', 0, 4),
(178, 'Isolates feature development', 1, 1), (178, 'Merges repositories', 0, 2), (178, 'Tracks commits', 0, 3), (178, 'Clones projects', 0, 4),
(179, 'Jenkins', 1, 1), (179, 'Docker', 0, 2), (179, 'Kubernetes', 0, 3), (179, 'Terraform', 0, 4),
(180, 'Build and test steps', 1, 1), (180, 'Deployment scripts', 0, 2), (180, 'Monitoring logs', 0, 3), (180, 'Security scans', 0, 4),
(181, 'Automates production releases', 1, 1), (181, 'Runs unit tests', 0, 2), (181, 'Monitors performance', 0, 3), (181, 'Manages containers', 0, 4),
(182, 'Includes production deployment', 1, 1), (182, 'Only builds code', 0, 2), (182, 'Tests databases', 0, 3), (182, 'Configures servers', 0, 4),
(183, 'Application and dependencies', 1, 1), (183, 'Source code only', 0, 2), (183, 'Configuration files', 0, 3), (183, 'Database schema', 0, 4),
(184, 'Defines container structure', 1, 1), (184, 'Runs containers', 0, 2), (184, 'Orchestrates containers', 0, 3), (184, 'Monitors containers', 0, 4),
(185, 'Container orchestration', 1, 1), (185, 'Version control', 0, 2), (185, 'Code testing', 0, 3), (185, 'Server monitoring', 0, 4),
(186, 'Manages pod scaling', 1, 1), (186, 'Builds images', 0, 2), (186, 'Monitors logs', 0, 3), (186, 'Configures networks', 0, 4),
(187, 'Manages infrastructure as code', 1, 1), (187, 'Runs containers', 0, 2), (187, 'Tests code', 0, 3), (187, 'Deploys apps', 0, 4),
(188, 'Terraform', 1, 1), (188, 'Docker', 0, 2), (188, 'Kubernetes', 0, 3), (188, 'Jenkins', 0, 4),
(189, 'Prometheus', 1, 1), (189, 'Grafana', 0, 2), (189, 'Terraform', 0, 3), (189, 'Ansible', 0, 4),
(190, 'Centralizes logs', 1, 1), (190, 'Monitors CPU', 0, 2), (190, 'Deploys apps', 0, 3), (190, 'Tests code', 0, 4),
(191, 'AWS Lambda', 1, 1), (191, 'EC2', 0, 2), (191, 'S3', 0, 3), (191, 'RDS', 0, 4),
(192, 'Automates resource management', 1, 1), (192, 'Stores data', 0, 2), (192, 'Runs containers', 0, 3), (192, 'Tests code', 0, 4),
(193, 'Cross-team communication', 1, 1), (193, 'Code optimization', 0, 2), (193, 'UI design', 0, 3), (193, 'Database indexing', 0, 4),
(194, 'Tracks code changes', 1, 1), (194, 'Deploys apps', 0, 2), (194, 'Monitors servers', 0, 3), (194, 'Tests databases', 0, 4),
-- Ethical Hacking (CourseID 11, QuizIDs 98-109, QuestionIDs 195-218)
(195, 'Identifies vulnerabilities', 1, 1), (195, 'Develops software', 0, 2), (195, 'Manages networks', 0, 3), (195, 'Designs UI', 0, 4),
(196, 'Simulates attacks', 1, 1), (196, 'Optimizes code', 0, 2), (196, 'Deploys apps', 0, 3), (196, 'Monitors traffic', 0, 4),
(197, 'Passive reconnaissance', 1, 1), (197, 'Active scanning', 0, 2), (197, 'Brute force attack', 0, 3), (197, 'SQL injection', 0, 4),
(198, 'Open-Source Intelligence', 1, 1), (198, 'Operational Security Intelligence', 0, 2), (198, 'Online System Inspection Tool', 0, 3), (198, 'Open-Source Integration Test', 0, 4),
(199, 'Nmap', 1, 1), (199, 'Wireshark', 0, 2), (199, 'Metasploit', 0, 3), (199, 'Burp Suite', 0, 4),
(200, 'Identifies open ports', 1, 1), (200, 'Captures packets', 0, 2), (200, 'Exploits vulnerabilities', 0, 3), (200, 'Tests passwords', 0, 4),
(201, 'System services', 1, 1), (201, 'Network packets', 0, 2), (201, 'User credentials', 0, 3), (201, 'Web vulnerabilities', 0, 4),
(202, 'SMB', 1, 1), (202, 'HTTP', 0, 2), (202, 'FTP', 0, 3), (202, 'SNMP', 0, 4),
(203, 'Nessus', 1, 1), (203, 'Nmap', 0, 2), (203, 'Wireshark', 0, 3), (203, 'John the Ripper', 0, 4),
(204, 'Vulnerability report', 1, 1), (204, 'Network map', 0, 2), (204, 'Packet capture', 0, 3), (204, 'Password list', 0, 4),
(205, 'Gains higher access', 1, 1), (205, 'Captures packets', 0, 2), (205, 'Steals data', 0, 3), (205, 'Denies service', 0, 4),
(206, 'Password cracking', 1, 1), (206, 'SQL injection', 0, 2), (206, 'XSS attack', 0, 3), (206, 'Packet sniffing', 0, 4),
(207, 'Captures keystrokes', 1, 1), (207, 'Encrypts files', 0, 2), (207, 'Steals cookies', 0, 3), (207, 'Blocks traffic', 0, 4),
(208, 'Legitimate software', 1, 1), (208, 'Malicious code', 0, 2), (208, 'Network scanner', 0, 3), (208, 'Firewall rule', 0, 4),
(209, 'Wireshark', 1, 1), (209, 'Nmap', 0, 2), (209, 'Metasploit', 0, 3), (209, 'Burp Suite', 0, 4),
(210, 'Analyzes network traffic', 1, 1), (210, 'Scans ports', 0, 2), (210, 'Exploits vulnerabilities', 0, 3), (210, 'Tests web apps', 0, 4),
(211, 'Email-based deception', 1, 1), (211, 'Password cracking', 0, 2), (211, 'Packet sniffing', 0, 3), (211, 'SQL injection', 0, 4),
(212, 'Gains trust with a false story', 1, 1), (212, 'Steals cookies', 0, 2), (212, 'Blocks traffic', 0, 3), (212, 'Scans networks', 0, 4),
(213, 'Disrupts service availability', 1, 1), (213, 'Steals data', 0, 2), (213, 'Encrypts files', 0, 3), (213, 'Exploits vulnerabilities', 0, 4),
(214, 'Uses multiple sources', 1, 1), (214, 'Targets a single server', 0, 2), (214, 'Captures packets', 0, 3), (214, 'Cracks passwords', 0, 4),
(215, 'Steals active sessions', 1, 1), (215, 'Blocks traffic', 0, 2), (215, 'Encrypts data', 0, 3), (215, 'Scans ports', 0, 4),
(216, 'Steals session tokens', 1, 1), (216, 'Blocks requests', 0, 2), (216, 'Encrypts data', 0, 3), (216, 'Scans networks', 0, 4),
(217, 'Injects malicious scripts', 1, 1), (217, 'Steals cookies', 0, 2), (217, 'Blocks traffic', 0, 3), (217, 'Cracks passwords', 0, 4),
(218, 'Manipulates SQL queries', 1, 1), (218, 'Captures packets', 0, 2), (218, 'Steals cookies', 0, 3), (218, 'Blocks traffic', 0, 4),
-- Deep Learning with TensorFlow (CourseID 12, QuizIDs 110-119, QuestionIDs 219-238)
(219, 'Neural networks', 1, 1), (219, 'Decision trees', 0, 2), (219, 'Linear regression', 0, 3), (219, 'Clustering', 0, 4),
(220, 'Adjusts weights', 1, 1), (220, 'Trains models', 0, 2), (220, 'Cleans data', 0, 3), (220, 'Visualizes data', 0, 4),
(221, 'Sequential', 1, 1), (221, 'Model', 0, 2), (221, 'Layer', 0, 3), (221, 'Optimizer', 0, 4),
(222, 'Simplifies model creation', 1, 1), (222, 'Optimizes training', 0, 2), (222, 'Processes data', 0, 3), (222, 'Deploys models', 0, 4),
(223, 'Fully connected layer', 1, 1), (223, 'Convolutional layer', 0, 2), (223, 'Recurrent layer', 0, 3), (223, 'Pooling layer', 0, 4),
(224, 'Introduces non-linearity', 1, 1), (224, 'Normalizes data', 0, 2), (224, 'Reduces dimensions', 0, 3), (224, 'Optimizes weights', 0, 4),
(225, 'Image classification', 1, 1), (225, 'Time-series prediction', 0, 2), (225, 'Text processing', 0, 3), (225, 'Clustering', 0, 4),
(226, 'Spatial dimensions', 1, 1), (226, 'Model weights', 0, 2), (226, 'Training time', 0, 3), (226, 'Data size', 0, 4),
(227, 'Processes sequential data', 1, 1), (227, 'Classifies images', 0, 2), (227, 'Reduces dimensions', 0, 3), (227, 'Optimizes models', 0, 4),
(228, 'Vanishing gradients', 1, 1), (228, 'Overfitting', 0, 2), (228, 'Underfitting', 0, 3), (228, 'Data drift', 0, 4),
(229, 'Uses pre-trained models', 1, 1), (229, 'Trains new models', 0, 2), (229, 'Cleans data', 0, 3), (229, 'Visualizes data', 0, 4),
(230, 'TensorFlow Hub', 1, 1), (230, 'TensorFlow Lite', 0, 2), (230, 'TensorFlow Serving', 0, 3), (230, 'TensorFlow Extended', 0, 4),
(231, 'Generator and discriminator', 1, 1), (231, 'Encoder and decoder', 0, 2), (231, 'Input and output', 0, 3), (231, 'Client and server', 0, 4),
(232, 'Classifies data as real or fake', 1, 1), (232, 'Generates new data', 0, 2), (232, 'Optimizes weights', 0, 3), (232, 'Reduces dimensions', 0, 4),
(233, 'TextVectorization', 1, 1), (233, 'ImageDataGenerator', 0, 2), (233, 'Dense', 0, 3), (233, 'Conv2D', 0, 4),
(234, 'Converts text to tokens', 1, 1), (234, 'Classifies text', 0, 2), (234, 'Generates text', 0, 3), (234, 'Encodes images', 0, 4),
(235, 'Identifies objects in images', 1, 1), (235, 'Classifies text', 0, 2), (235, 'Predicts sequences', 0, 3), (235, 'Clusters data', 0, 4),
(236, 'tf.image', 1, 1), (236, 'tf.text', 0, 2), (236, 'tf.data', 0, 3), (236, 'tf.keras', 0, 4),
(237, 'Model deployment', 1, 1), (237, 'Data preprocessing', 0, 2), (237, 'Model training', 0, 3), (237, 'Data visualization', 0, 4),
(238, 'Reduces model size', 1, 1), (238, 'Increases accuracy', 0, 2), (238, 'Speeds training', 0, 3), (238, 'Cleans data', 0, 4),
-- Blockchain Development (CourseID 13, QuizIDs 120-129, QuestionIDs 239-258)
(239, 'Cryptographic hashing', 1, 1), (239, 'Data encryption', 0, 2), (239, 'Smart contracts', 0, 3), (239, 'Consensus algorithms', 0, 4),
(240, 'Contains metadata', 1, 1), (240, 'Stores transactions', 0, 2), (240, 'Secures keys', 0, 3), (240, 'Validates nodes', 0, 4),
(241, 'Hashing', 1, 1), (241, 'Encryption', 0, 2), (241, 'Digital signatures', 0, 3), (241, 'Consensus protocols', 0, 4),
(242, 'Signs transactions', 1, 1), (242, 'Encrypts data', 0, 2), (242, 'Stores blocks', 0, 3), (242, 'Validates nodes', 0, 4),
(243, 'Decentralized currency', 1, 1), (243, 'Smart contracts', 0, 2), (243, 'DApp platform', 0, 3), (243, 'Data storage', 0, 4),
(244, 'Digital signatures', 1, 1), (244, 'Hashing', 0, 2), (244, 'Encryption', 0, 3), (244, 'Consensus', 0, 4),
(245, 'Smart contracts', 1, 1), (245, 'Cryptocurrency', 0, 2), (245, 'Data storage', 0, 3), (245, 'Network security', 0, 4),
(246, 'Pays for transactions', 1, 1), (246, 'Stores data', 0, 2), (246, 'Secures keys', 0, 3), (246, 'Validates blocks', 0, 4),
(247, 'contract', 1, 1), (247, 'function', 0, 2), (247, 'struct', 0, 3), (247, 'event', 0, 4),
(248, 'Allows Ether transfers', 1, 1), (248, 'Logs events', 0, 2), (248, 'Defines variables', 0, 3), (248, 'Calls functions', 0, 4),
(249, 'Runs on blockchain', 1, 1), (249, 'Runs on servers', 0, 2), (249, 'Stores data', 0, 3), (249, 'Tests code', 0, 4),
(250, 'Blockchain nodes', 1, 1), (250, 'Web servers', 0, 2), (250, 'Databases', 0, 3), (250, 'APIs', 0, 4),
(251, 'Truffle', 1, 1), (251, 'Remix', 0, 2), (251, 'Hardhat', 0, 3), (251, 'MetaMask', 0, 4),
(252, 'Simulates blockchain', 1, 1), (252, 'Deploys contracts', 0, 2), (252, 'Stores keys', 0, 3), (252, 'Signs transactions', 0, 4),
(253, 'Recursive contract calls', 1, 1), (253, 'SQL injection', 0, 2), (253, 'XSS attack', 0, 3), (253, 'Packet sniffing', 0, 4),
(254, 'Prevents infinite loops', 1, 1), (254, 'Encrypts data', 0, 2), (254, 'Signs transactions', 0, 3), (254, 'Stores blocks', 0, 4),
(255, 'Ethereum', 1, 1), (255, 'Bitcoin', 0, 2), (255, 'Litecoin', 0, 3), (255, 'Ripple', 0, 4),
(256, 'Develops smart contracts', 1, 1), (256, 'Stores keys', 0, 2), (256, 'Signs transactions', 0, 3), (256, 'Tests networks', 0, 4),
(257, 'Wallet for Ethereum', 1, 1), (257, 'Smart contract IDE', 0, 2), (257, 'Blockchain tester', 0, 3), (257, 'Network scanner', 0, 4),
(258, 'Executes smart contracts', 1, 1), (258, 'Stores blocks', 0, 2), (258, 'Validates transactions', 0, 3), (258, 'Encrypts data', 0, 4),
-- Internet of Things Fundamentals (CourseID 14, QuizIDs 130-139, QuestionIDs 259-278)
(259, 'Network protocols', 1, 1), (259, 'Physical cables', 0, 2), (259, 'Cloud servers', 0, 3), (259, 'USB interfaces', 0, 4),
(260, 'MQTT', 1, 1), (260, 'HTTP', 0, 2), (260, 'FTP', 0, 3), (260, 'SMTP', 0, 4),
(261, 'Thermistor', 1, 1), (261, 'Photocell', 0, 2), (261, 'Gyroscope', 0, 3), (261, 'Accelerometer', 0, 4),
(262, 'Controls physical devices', 1, 1), (262, 'Senses environment', 0, 2), (262, 'Stores data', 0, 3), (262, 'Processes data', 0, 4),
(263, 'Prototypes IoT devices', 1, 1), (263, 'Runs servers', 0, 2), (263, 'Stores data', 0, 3), (263, 'Analyzes data', 0, 4),
(264, 'Initializes hardware', 1, 1), (264, 'Reads sensors', 0, 2), (264, 'Sends data', 0, 3), (264, 'Controls actuators', 0, 4),
(265, 'Runs IoT applications', 1, 1), (265, 'Senses environment', 0, 2), (265, 'Stores data', 0, 3), (265, 'Encrypts data', 0, 4),
(266, 'Controls hardware', 1, 1), (266, 'Reads sensors', 0, 2), (266, 'Sends data', 0, 3), (266, 'Stores data', 0, 4),
(267, 'Enables device communication', 1, 1), (267, 'Stores data', 0, 2), (267, 'Analyzes data', 0, 3), (267, 'Encrypts data', 0, 4),
(268, 'Efficient data exchange', 1, 1), (268, 'Data storage', 0, 2), (268, 'Data encryption', 0, 3), (268, 'Data visualization', 0, 4),
(269, 'AWS IoT Core', 1, 1), (269, 'AWS Lambda', 0, 2), (269, 'AWS S3', 0, 3), (269, 'AWS EC2', 0, 4),
(270, 'Processes IoT data', 1, 1), (270, 'Stores IoT data', 0, 2), (270, 'Encrypts IoT data', 0, 3), (270, 'Visualizes IoT data', 0, 4),
(271, 'Unauthorized access', 1, 1), (271, 'Data overflow', 0, 2), (271, 'Packet loss', 0, 3), (271, 'Slow processing', 0, 4),
(272, 'Secures data transmission', 1, 1), (272, 'Stores data', 0, 2), (272, 'Analyzes data', 0, 3), (272, 'Sends data', 0, 4),
(273, 'Processes data locally', 1, 1), (273, 'Stores data', 0, 2), (273, 'Sends data', 0, 3), (273, 'Encrypts data', 0, 4),
(274, 'Apache Kafka', 1, 1), (274, 'Apache Spark', 0, 2), (274, 'Hadoop', 0, 3), (274, 'Flink', 0, 4),
(275, 'Device interoperability', 1, 1), (275, 'Data storage', 0, 2), (275, 'Data encryption', 0, 3), (275, 'Data visualization', 0, 4),
(276, 'Manages protocol translation', 1, 1), (276, 'Stores data', 0, 2), (276, 'Analyzes data', 0, 3), (276, 'Encrypts data', 0, 4),
(277, 'Zigbee', 1, 1), (277, 'HTTP', 0, 2), (277, 'FTP', 0, 3), (277, 'SMTP', 0, 4),
(278, 'Centralizes device control', 1, 1), (278, 'Stores data', 0, 2), (278, 'Encrypts data', 0, 3), (278, 'Analyzes data', 0, 4),
-- Vue.js for Frontend Development (CourseID 15, QuizIDs 140-149, QuestionIDs 279-298)
(279, 'Reactive data binding', 1, 1), (279, 'Server-side rendering', 0, 2), (279, 'Database management', 0, 3), (279, 'API fetching', 0, 4),
(280, 'Manages reactive data', 1, 1), (280, 'Handles routing', 0, 2), (280, 'Styles components', 0, 3), (280, 'Fetches data', 0, 4),
(281, 'Template, script, style', 1, 1), (281, 'HTML, CSS, JS', 0, 2), (281, 'Model, view, controller', 0, 3), (281, 'Data, props, methods', 0, 4),
(282, 'Passes data to components', 1, 1), (282, 'Styles components', 0, 2), (282, 'Handles events', 0, 3), (282, 'Fetches data', 0, 4),
(283, 'v-bind', 1, 1), (283, 'v-model', 0, 2), (283, 'v-if', 0, 3), (283, 'v-for', 0, 4),
(284, 'Conditional rendering', 1, 1), (284, 'Data binding', 0, 2), (284, 'Event handling', 0, 3), (284, 'List rendering', 0, 4),
(285, 'Reactivity system', 1, 1), (285, 'Routing system', 0, 2), (285, 'State management', 0, 3), (285, 'API fetching', 0, 4),
(286, 'Caches derived data', 1, 1), (286, 'Handles events', 0, 2), (286, 'Fetches data', 0, 3), (286, 'Styles components', 0, 4),
(287, 'Client-side navigation', 1, 1), (287, 'State management', 0, 2), (287, 'Data binding', 0, 3), (287, 'API fetching', 0, 4),
(288, 'Handles URL parameters', 1, 1), (288, 'Styles routes', 0, 2), (288, 'Fetches data', 0, 3), (288, 'Manages state', 0, 4),
(289, 'Centralized state management', 1, 1), (289, 'Handles routing', 0, 2), (289, 'Fetches data', 0, 3), (289, 'Styles components', 0, 4),
(290, 'Updates state synchronously', 1, 1), (290, 'Fetches data', 0, 2), (290, 'Handles events', 0, 3), (290, 'Renders UI', 0, 4),
(291, 'Two-way data binding', 1, 1), (291, 'Conditional rendering', 0, 2), (291, 'Event handling', 0, 3), (291, 'List rendering', 0, 4),
(292, 'Customizes form validation', 1, 1), (292, 'Styles forms', 0, 2), (292, 'Fetches data', 0, 3), (292, 'Handles routing', 0, 4),
(293, 'Component-based structure', 1, 1), (293, 'Global state', 0, 2), (293, 'Inline styles', 0, 3), (293, 'API fetching', 0, 4),
(294, 'Encapsulates component logic', 1, 1), (294, 'Handles routing', 0, 2), (294, 'Fetches data', 0, 3), (294, 'Styles pages', 0, 4),
(295, 'Vue Test Utils', 1, 1), (295, 'Jest', 0, 2), (295, 'Mocha', 0, 3), (295, 'Cypress', 0, 4),
(296, 'Component output consistency', 1, 1), (296, 'API responses', 0, 2), (296, 'Routing logic', 0, 3), (296, 'State changes', 0, 4),
(297, 'Project scaffolding', 1, 1), (297, 'Component testing', 0, 2), (297, 'Data fetching', 0, 3), (297, 'State management', 0, 4),
(298, 'Optimizes for production', 1, 1), (298, 'Tests components', 0, 2), (298, 'Fetches data', 0, 3), (298, 'Handles routing', 0, 4),
-- Django Web Framework (CourseID 16, QuizIDs 150-159, QuestionIDs 299-318)
(299, 'Builds web applications', 1, 1), (299, 'Manages databases', 0, 2), (299, 'Tests APIs', 0, 3), (299, 'Styles pages', 0, 4),
(300, 'Project configurations', 1, 1), (300, 'Database models', 0, 2), (300, 'URL routes', 0, 3), (300, 'Templates', 0, 4),
(301, 'Database table', 1, 1), (301, 'Web view', 0, 2), (301, 'URL route', 0, 3), (301, 'Template', 0, 4),
(302, 'Applies schema changes', 1, 1), (302, 'Renders templates', 0, 2), (302, 'Handles requests', 0, 3), (302, 'Tests code', 0, 4),
(303, 'Processes HTTP requests', 1, 1), (303, 'Renders templates', 0, 2), (303, 'Manages models', 0, 3), (303, 'Tests code', 0, 4),
(304, 'Renders HTML pages', 1, 1), (304, 'Handles requests', 0, 2), (304, 'Manages models', 0, 3), (304, 'Tests code', 0, 4),
(305, 'User input data', 1, 1), (305, 'Database queries', 0, 2), (305, 'URL routes', 0, 3), (305, 'Templates', 0, 4),
(306, 'Binds forms to models', 1, 1), (306, 'Renders templates', 0, 2), (306, 'Handles requests', 0, 3), (306, 'Tests code', 0, 4),
(307, 'Manages site administration', 1, 1), (307, 'Renders templates', 0, 2), (307, 'Handles requests', 0, 3), (307, 'Tests code', 0, 4),
(308, 'python manage.py createsuperuser', 1, 1), (308, 'python manage.py migrate', 0, 2), (308, 'python manage.py runserver', 0, 3), (308, 'python manage.py test', 0, 4),
(309, 'django.contrib.auth', 1, 1), (309, 'django.contrib.admin', 0, 2), (309, 'django.urls', 0, 3), (309, 'django.forms', 0, 4),
(310, 'Restricts view access', 1, 1), (310, 'Handles forms', 0, 2), (310, 'Renders templates', 0, 3), (310, 'Tests code', 0, 4),
(311, 'RESTful APIs', 1, 1), (311, 'Web templates', 0, 2), (311, 'Database models', 0, 3), (311, 'Unit tests', 0, 4),
(312, 'Converts data to JSON', 1, 1), (312, 'Renders templates', 0, 2), (312, 'Handles requests', 0, 3), (312, 'Tests code', 0, 4),
(313, 'TestClient', 1, 1), (313, 'TestCase', 0, 2), (313, 'SimpleTestCase', 0, 3), (313, 'LiveServerTestCase', 0, 4),
(314, 'Simulates HTTP requests', 1, 1), (314, 'Renders templates', 0, 2), (314, 'Manages models', 0, 3), (314, 'Tests databases', 0, 4),
(315, 'Runs WSGI applications', 1, 1), (315, 'Serves static files', 0, 2), (315, 'Manages databases', 0, 3), (315, 'Tests code', 0, 4),
(316, 'Serves static files', 1, 1), (316, 'Runs applications', 0, 2), (316, 'Manages databases', 0, 3), (316, 'Tests code', 0, 4),
(317, 'Reusable module', 1, 1), (317, 'Database table', 0, 2), (317, 'Web view', 0, 3), (317, 'Template', 0, 4),
(318, 'Django commands', 1, 1), (318, 'URL routes', 0, 2), (318, 'Database models', 0, 3), (318, 'Templates', 0, 4);

-- Insert QuizAttempts
INSERT INTO QuizAttempts (QuizID, CustomerID, StartTime, EndTime, Score, IsPassed)
VALUES
(1, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -10, DATEADD(minute, 20, GETDATE())), 80, 1),
(1, 2, DATEADD(day, -12, GETDATE()), DATEADD(day, -12, DATEADD(minute, 15, GETDATE())), 90, 1),
(2, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -9, DATEADD(minute, 18, GETDATE())), 70, 1),
(3, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -8, DATEADD(minute, 25, GETDATE())), 60, 0),
(4, 3, DATEADD(day, -7, GETDATE()), DATEADD(day, -7, DATEADD(minute, 20, GETDATE())), 85, 1),
(5, 3, DATEADD(day, -6, GETDATE()), DATEADD(day, -6, DATEADD(minute, 22, GETDATE())), 75, 1);

-- Insert UserAnswers
INSERT INTO UserAnswers (AttemptID, QuestionID, AnswerID, IsCorrect)
VALUES
(1, 1, 1, 1),
(1, 2, 2, 0),
(2, 1, 1, 1),
(2, 2, 5, 1),
(3, 3, 9, 1),
(3, 4, 14, 0),
(4, 5, 17, 1),
(4, 6, 22, 0),
(5, 7, 25, 1),
(5, 8, 29, 1);

-- Insert into CourseProgress
INSERT INTO CourseProgress (CustomerID, CourseID, LastAccessDate, CompletionPercentage, IsCompleted)
VALUES
(4, 1, GETDATE(), 50.0, 0), -- Customer 4, Course 1, 50% complete
(5, 1, DATEADD(day, -2, GETDATE()), 75.0, 0), -- Customer 5, Course 1, 75% complete
(6, 6, GETDATE(), 50.0, 0), -- Customer 6, Course 6, 50% complete
(7, 15, DATEADD(day, -1, GETDATE()), 30.0, 0), -- Customer 7, Course 15, 30% complete
(8, 16, DATEADD(day, -3, GETDATE()), 50.0, 0), -- Customer 8, Course 16, 50% complete
(9, 1, DATEADD(day, -4, GETDATE()), 25.0, 0), -- Customer 9, Course 1, 25% complete
(10, 15, GETDATE(), 80.0, 0), -- Customer 10, Course 15, 80% complete
(11, 6, DATEADD(day, -2, GETDATE()), 100.0, 1), -- Customer 11, Course 6, 100% complete
(12, 16, DATEADD(day, -1, GETDATE()), 30.0, 0); -- Customer 12, Course 16, 30% complete

-- Insert into LessonProgress
-- CourseID 1 (12 lessons, LessonID 1-12)
-- Customer 4, Course 1, 50% (6/12 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(4, 1, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())),
(4, 2, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())),
(4, 3, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())),
(4, 4, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())),
(4, 5, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())),
(4, 6, 1, DATEADD(day, -5, GETDATE()), GETDATE()),
(4, 7, 0, NULL, GETDATE()),
(4, 8, 0, NULL, GETDATE()),
(4, 9, 0, NULL, GETDATE()),
(4, 10, 0, NULL, GETDATE()),
(4, 11, 0, NULL, GETDATE()),
(4, 12, 0, NULL, GETDATE());

-- Customer 5, Course 1, 75% (9/12 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(5, 1, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())),
(5, 2, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())),
(5, 3, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())),
(5, 4, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())),
(5, 5, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())),
(5, 6, 1, DATEADD(day, -10, GETDATE()), GETDATE()),
(5, 7, 1, DATEADD(day, -9, GETDATE()), GETDATE()),
(5, 8, 1, DATEADD(day, -8, GETDATE()), GETDATE()),
(5, 9, 1, DATEADD(day, -7, GETDATE()), GETDATE()),
(5, 10, 0, NULL, GETDATE()),
(5, 11, 0, NULL, GETDATE()),
(5, 12, 0, NULL, GETDATE());

-- Customer 9, Course 1, 25% (3/12 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(9, 1, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -4, GETDATE())),
(9, 2, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -3, GETDATE())),
(9, 3, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())),
(9, 4, 0, NULL, GETDATE()),
(9, 5, 0, NULL, GETDATE()),
(9, 6, 0, NULL, GETDATE()),
(9, 7, 0, NULL, GETDATE()),
(9, 8, 0, NULL, GETDATE()),
(9, 9, 0, NULL, GETDATE()),
(9, 10, 0, NULL, GETDATE()),
(9, 11, 0, NULL, GETDATE()),
(9, 12, 0, NULL, GETDATE());

-- CourseID 6 (10 lessons, LessonID 49-58)
-- Customer 6, Course 6, 50% (5/10 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(6, 49, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())),
(6, 50, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())),
(6, 51, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())),
(6, 52, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())),
(6, 53, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())),
(6, 54, 0, NULL, GETDATE()),
(6, 55, 0, NULL, GETDATE()),
(6, 56, 0, NULL, GETDATE()),
(6, 57, 0, NULL, GETDATE()),
(6, 58, 0, NULL, GETDATE());

-- Customer 11, Course 6, 100% (10/10 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(11, 49, 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -10, GETDATE())),
(11, 50, 1, DATEADD(day, -19, GETDATE()), DATEADD(day, -9, GETDATE())),
(11, 51, 1, DATEADD(day, -18, GETDATE()), DATEADD(day, -8, GETDATE())),
(11, 52, 1, DATEADD(day, -17, GETDATE()), DATEADD(day, -7, GETDATE())),
(11, 53, 1, DATEADD(day, -16, GETDATE()), DATEADD(day, -6, GETDATE())),
(11, 54, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())),
(11, 55, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())),
(11, 56, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())),
(11, 57, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())),
(11, 58, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE()));

-- CourseID 15 (10 lessons, LessonID 140-149)
-- Customer 7, Course 15, 30% (3/10 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(7, 140, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())),
(7, 141, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())),
(7, 142, 1, DATEADD(day, -4, GETDATE()), GETDATE()),
(7, 143, 0, NULL, GETDATE()),
(7, 144, 0, NULL, GETDATE()),
(7, 145, 0, NULL, GETDATE()),
(7, 146, 0, NULL, GETDATE()),
(7, 147, 0, NULL, GETDATE()),
(7, 148, 0, NULL, GETDATE()),
(7, 149, 0, NULL, GETDATE());

-- Customer 10, Course 15, 75% (7.5/10 lessons, rounded to 8 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(10, 140, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())),
(10, 141, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())),
(10, 142, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())),
(10, 143, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())),
(10, 144, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())),
(10, 145, 1, DATEADD(day, -10, GETDATE()), GETDATE()),
(10, 146, 1, DATEADD(day, -9, GETDATE()), GETDATE()),
(10, 147, 1, DATEADD(day, -8, GETDATE()), GETDATE()),
(10, 148, 0, NULL, GETDATE()),
(10, 149, 0, NULL, GETDATE());

-- CourseID 16 (10 lessons, LessonID 150-159)
-- Customer 8, Course 16, 50% (5/10 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(8, 150, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())),
(8, 151, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())),
(8, 152, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())),
(8, 153, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())),
(8, 154, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())),
(8, 155, 0, NULL, GETDATE()),
(8, 156, 0, NULL, GETDATE()),
(8, 157, 0, NULL, GETDATE()),
(8, 158, 0, NULL, GETDATE()),
(8, 159, 0, NULL, GETDATE());

-- Customer 12, Course 16, 25% (2.5/10 lessons, rounded to 3 lessons completed)
INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(12, 150, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())),
(12, 151, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())),
(12, 152, 1, DATEADD(day, -4, GETDATE()), GETDATE()),
(12, 153, 0, NULL, GETDATE()),
(12, 154, 0, NULL, GETDATE()),
(12, 155, 0, NULL, GETDATE()),
(12, 156, 0, NULL, GETDATE()),
(12, 157, 0, NULL, GETDATE()),
(12, 158, 0, NULL, GETDATE()),
(12, 159, 0, NULL, GETDATE());

-- Insert into LessonItemProgress
-- Customer 4, Course 1, Lessons 1-6 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(4, 1, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Video for Lesson 1
(4, 2, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Quiz for Lesson 1
(4, 3, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Material for Lesson 1
(4, 4, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 2
(4, 5, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 2
(4, 6, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 2
(4, 7, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 3
(4, 8, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 3
(4, 9, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 3
(4, 10, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 4
(4, 11, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 4
(4, 12, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 4
(4, 13, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 5
(4, 14, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 5
(4, 15, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Material for Lesson 5
(4, 16, 1, DATEADD(day, -5, GETDATE()), GETDATE()), -- Video for Lesson 6
(4, 17, 1, DATEADD(day, -5, GETDATE()), GETDATE()), -- Quiz for Lesson 6
(4, 18, 1, DATEADD(day, -5, GETDATE()), GETDATE()); -- Material for Lesson 6

-- Customer 5, Course 1, Lessons 1-9 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(5, 1, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Video for Lesson 1
(5, 2, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Quiz for Lesson 1
(5, 3, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Material for Lesson 1
(5, 4, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 2
(5, 5, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 2
(5, 6, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 2
(5, 7, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 3
(5, 8, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 3
(5, 9, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 3
(5, 10, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 4
(5, 11, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 4
(5, 12, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 4
(5, 13, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 5
(5, 14, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 5
(5, 15, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Material for Lesson 5
(5, 16, 1, DATEADD(day, -10, GETDATE()), GETDATE()), -- Video for Lesson 6
(5, 17, 1, DATEADD(day, -10, GETDATE()), GETDATE()), -- Quiz for Lesson 6
(5, 18, 1, DATEADD(day, -10, GETDATE()), GETDATE()), -- Material for Lesson 6
(5, 19, 1, DATEADD(day, -9, GETDATE()), GETDATE()), -- Video for Lesson 7
(5, 20, 1, DATEADD(day, -9, GETDATE()), GETDATE()), -- Quiz for Lesson 7
(5, 21, 1, DATEADD(day, -9, GETDATE()), GETDATE()), -- Material for Lesson 7
(5, 22, 1, DATEADD(day, -8, GETDATE()), GETDATE()), -- Video for Lesson 8
(5, 23, 1, DATEADD(day, -8, GETDATE()), GETDATE()), -- Quiz for Lesson 8
(5, 24, 1, DATEADD(day, -8, GETDATE()), GETDATE()), -- Material for Lesson 8
(5, 25, 1, DATEADD(day, -7, GETDATE()), GETDATE()), -- Video for Lesson 9
(5, 26, 1, DATEADD(day, -7, GETDATE()), GETDATE()), -- Quiz for Lesson 9
(5, 27, 1, DATEADD(day, -7, GETDATE()), GETDATE()); -- Material for Lesson 9

-- Customer 9, Course 1, Lessons 1-3 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(9, 1, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 1
(9, 2, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 1
(9, 3, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 1
(9, 4, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 2
(9, 5, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 2
(9, 6, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 2
(9, 7, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 3
(9, 8, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 3
(9, 9, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())); -- Material for Lesson 3

-- Customer 6, Course 6, Lessons 49-53 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(6, 145, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Video for Lesson 49
(6, 146, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Quiz for Lesson 49
(6, 147, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Material for Lesson 49
(6, 148, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 50
(6, 149, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 50
(6, 150, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 50
(6, 151, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 51
(6, 152, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 51
(6, 153, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 51
(6, 154, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 52
(6, 155, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 52
(6, 156, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 52
(6, 157, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 53
(6, 158, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 53
(6, 159, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())); -- Material for Lesson 53

-- Customer 11, Course 6, Lessons 49-58 completed, LessonItem 145-
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(11, 145, 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -10, GETDATE())), -- Video for Lesson 49
(11, 146, 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -10, GETDATE())), -- Quiz for Lesson 49
(11, 147, 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -10, GETDATE())), -- Material for Lesson 49
(11, 148, 1, DATEADD(day, -19, GETDATE()), DATEADD(day, -9, GETDATE())), -- Video for Lesson 50
(11, 149, 1, DATEADD(day, -19, GETDATE()), DATEADD(day, -9, GETDATE())), -- Quiz for Lesson 50
(11, 150, 1, DATEADD(day, -19, GETDATE()), DATEADD(day, -9, GETDATE())), -- Material for Lesson 50
(11, 151, 1, DATEADD(day, -18, GETDATE()), DATEADD(day, -8, GETDATE())), -- Video for Lesson 51
(11, 152, 1, DATEADD(day, -18, GETDATE()), DATEADD(day, -8, GETDATE())), -- Quiz for Lesson 51
(11, 153, 1, DATEADD(day, -18, GETDATE()), DATEADD(day, -8, GETDATE())), -- Material for Lesson 51
(11, 154, 1, DATEADD(day, -17, GETDATE()), DATEADD(day, -7, GETDATE())), -- Video for Lesson 52
(11, 155, 1, DATEADD(day, -17, GETDATE()), DATEADD(day, -7, GETDATE())), -- Quiz for Lesson 52
(11, 156, 1, DATEADD(day, -17, GETDATE()), DATEADD(day, -7, GETDATE())), -- Material for Lesson 52
(11, 157, 1, DATEADD(day, -16, GETDATE()), DATEADD(day, -6, GETDATE())), -- Video for Lesson 53
(11, 158, 1, DATEADD(day, -16, GETDATE()), DATEADD(day, -6, GETDATE())), -- Quiz for Lesson 53
(11, 159, 1, DATEADD(day, -16, GETDATE()), DATEADD(day, -6, GETDATE())), -- Material for Lesson 53
(11, 160, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Video for Lesson 54
(11, 161, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Quiz for Lesson 54
(11, 162, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Material for Lesson 54
(11, 163, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 55
(11, 164, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 55
(11, 165, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 55
(11, 166, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 56
(11, 167, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 56
(11, 168, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 56
(11, 169, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 57
(11, 170, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 57
(11, 171, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 57
(11, 172, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 58
(11, 173, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 58
(11, 174, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())); -- Material for Lesson 58

-- CourseID 15, LessonID 140-149, LessonItems 418-426
-- Customer 7, Course 15, Lessons 140-142 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(7, 418, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 140
(7, 419, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 140
(7, 420, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 140
(7, 421, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 141
(7, 422, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 141
(7, 423, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())), -- Material for Lesson 141
(7, 424, 1, DATEADD(day, -4, GETDATE()), GETDATE()), -- Video for Lesson 142
(7, 425, 1, DATEADD(day, -4, GETDATE()), GETDATE()), -- Quiz for Lesson 142
(7, 426, 1, DATEADD(day, -4, GETDATE()), GETDATE()); -- Material for Lesson 142

-- Customer 10, Course 15, Lessons 140-147 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(10, 418, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Video for Lesson 140
(10, 419, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Quiz for Lesson 140
(10, 420, 1, DATEADD(day, -15, GETDATE()), DATEADD(day, -5, GETDATE())), -- Material for Lesson 140
(10, 421, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 141
(10, 422, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 141
(10, 423, 1, DATEADD(day, -14, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 141
(10, 424, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 142
(10, 425, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 142
(10, 426, 1, DATEADD(day, -13, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 142
(10, 427, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 143
(10, 428, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 143
(10, 429, 1, DATEADD(day, -12, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 143
(10, 430, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 144
(10, 431, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 144
(10, 432, 1, DATEADD(day, -11, GETDATE()), DATEADD(day, -1, GETDATE())), -- Material for Lesson 144
(10, 433, 1, DATEADD(day, -10, GETDATE()), GETDATE()), -- Video for Lesson 145
(10, 434, 1, DATEADD(day, -10, GETDATE()), GETDATE()), -- Quiz for Lesson 145
(10, 435, 1, DATEADD(day, -10, GETDATE()), GETDATE()), -- Material for Lesson 145
(10, 436, 1, DATEADD(day, -9, GETDATE()), GETDATE()), -- Video for Lesson 146
(10, 437, 1, DATEADD(day, -9, GETDATE()), GETDATE()), -- Quiz for Lesson 146
(10, 438, 1, DATEADD(day, -9, GETDATE()), GETDATE()), -- Material for Lesson 146
(10, 439, 1, DATEADD(day, -8, GETDATE()), GETDATE()), -- Video for Lesson 147
(10, 440, 1, DATEADD(day, -8, GETDATE()), GETDATE()), -- Quiz for Lesson 147
(10, 441, 1, DATEADD(day, -8, GETDATE()), GETDATE()); -- Material for Lesson 147

-- CourseID 16, LessonID 150-159, LessonItems 448-462
-- Customer 8, Course 16, Lessons 150-154 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(8, 448, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Video for Lesson 150
(8, 449, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Quiz for Lesson 150
(8, 450, 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -5, GETDATE())), -- Material for Lesson 150
(8, 451, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Video for Lesson 151
(8, 452, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Quiz for Lesson 151
(8, 453, 1, DATEADD(day, -9, GETDATE()), DATEADD(day, -4, GETDATE())), -- Material for Lesson 151
(8, 454, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Video for Lesson 152
(8, 455, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Quiz for Lesson 152
(8, 456, 1, DATEADD(day, -8, GETDATE()), DATEADD(day, -3, GETDATE())), -- Material for Lesson 152
(8, 457, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 153
(8, 458, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 153
(8, 459, 1, DATEADD(day, -7, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 153
(8, 460, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 154
(8, 461, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 154
(8, 462, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -1, GETDATE())); -- Material for Lesson 154

-- Customer 12, Course 16, Lessons 150-152 completed
INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate)
VALUES
(12, 448, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Video for Lesson 150
(12, 449, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Quiz for Lesson 150
(12, 450, 1, DATEADD(day, -6, GETDATE()), DATEADD(day, -2, GETDATE())), -- Material for Lesson 150
(12, 451, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())), -- Video for Lesson 151
(12, 452, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())), -- Quiz for Lesson 151
(12, 453, 1, DATEADD(day, -5, GETDATE()), DATEADD(day, -1, GETDATE())), -- Material for Lesson 151
(12, 454, 1, DATEADD(day, -4, GETDATE()), GETDATE()), -- Video for Lesson 152
(12, 455, 1, DATEADD(day, -4, GETDATE()), GETDATE()), -- Quiz for Lesson 152
(12, 456, 1, DATEADD(day, -4, GETDATE()), GETDATE()); -- Material for Lesson 152

