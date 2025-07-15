<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>${quiz.title} - LightHouse</title>
        <!-- Include common head with styles -->
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            body {
                background-color: var(--bg-light);
            }

            .quiz-container {
                max-width: 1200px;
                margin: 2rem auto;
                padding: 1rem;
                display: flex;
                gap: 1.5rem;
            }

            /* Left Column - Info Panel */
            .quiz-info-panel {
                width: 320px;
                flex-shrink: 0;
            }

            .quiz-header {
                position: sticky;
                top: 0;
                background-color: var(--bg-white);
                border-radius: 10px;
                box-shadow: var(--box-shadow);
                padding: 1.5rem;
                margin-bottom: 1.5rem;
            }

            .quiz-title {
                margin-bottom: 1rem;
                font-weight: 700;
                font-size: 1.5rem;
                color: var(--primary-color);
            }

            .quiz-meta {
                margin-bottom: 1.5rem;
            }

            .quiz-meta-item {
                display: flex;
                align-items: center;
                gap: 0.5rem;
                margin-bottom: 0.75rem;
                color: var(--text-medium);
            }

            .timer-container {
                margin: 1.5rem 0;
            }

            .timer {
                background-color: var(--primary-color);
                color: white;
                padding: 0.75rem;
                border-radius: 10px;
                font-weight: 600;
                text-align: center;
                margin-bottom: 1rem;
                font-size: 1.2rem;
            }

            .timer.warning {
                background-color: var(--warning);
            }

            .timer.danger {
                background-color: var(--danger);
                animation: pulse 1s infinite;
            }

            .quiz-progress-text {
                font-size: 0.9rem;
                color: var(--text-medium);
                text-align: center;
                margin-bottom: 0.5rem;
            }

            .progress-bar-container {
                height: 6px;
                background-color: var(--secondary-light);
                border-radius: 3px;
                overflow: hidden;
            }

            .progress-bar-fill {
                height: 100%;
                background-color: var(--primary-color);
                transition: width 0.3s ease;
            }

            .quiz-actions {
                margin-top: 1.5rem;
            }

            .submit-button {
                width: 100%;
                background-color: var(--success);
                color: white;
                border: none;
                border-radius: 6px;
                padding: 0.75rem;
                font-weight: 600;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 0.5rem;
                transition: all 0.2s ease;
            }

            .submit-button:hover {
                background-color: #2d9147;
            }

            .submit-button:disabled {
                background-color: var(--text-light);
                cursor: not-allowed;
            }

            .quiz-info-box {
                background-color: var(--bg-white);
                border-radius: 10px;
                box-shadow: var(--box-shadow);
                padding: 1.5rem;
                margin-top: 1.5rem;
                border-left: 5px solid var(--primary-color);
            }

            .quiz-info-title {
                font-weight: 600;
                margin-bottom: 0.75rem;
                color: var(--primary-color);
            }

            .quiz-info-text {
                color: var(--text-medium);
                font-size: 0.9rem;
                line-height: 1.5;
            }

            /* Right Column - Questions List */
            .questions-container {
                flex-grow: 1;
            }

            .question-card {
                background-color: var(--bg-white);
                border-radius: 10px;
                box-shadow: var(--box-shadow);
                padding: 1.5rem;
                margin-bottom: 1.5rem;
                border-left: 5px solid var(--primary-color);
            }

            .question-number {
                font-weight: 600;
                color: var(--primary-color);
                margin-bottom: 0.5rem;
                display: flex;
                justify-content: space-between;
            }

            .question-status {
                display: inline-flex;
                align-items: center;
                font-size: 0.85rem;
                color: var(--text-medium);
            }

            .status-answered {
                color: var(--success);
            }

            .question-content {
                font-size: 1.1rem;
                font-weight: 500;
                margin-bottom: 1.5rem;
            }

            /* Answer Options */
            .answer-options {
                display: flex;
                flex-direction: column;
                gap: 0.75rem;
            }

            .answer-option {
                position: relative;
                border: 1px solid var(--text-light);
                border-radius: 6px;
                padding: 0.25rem;
                transition: all 0.2s ease;
            }

            .answer-option:hover {
                border-color: var(--primary-color);
                background-color: rgba(var(--primary-color-rgb), 0.05);
            }

            .answer-option input[type="radio"] {
                position: absolute;
                opacity: 0;
                width: 100%;
                height: 100%;
                cursor: pointer;
                margin: -0.25rem;
            }

            .answer-option input[type="radio"]:checked+label {
                font-weight: 500;
            }

            .answer-option input[type="radio"]:checked+label:before {
                border-color: var(--primary-color);
                background-color: var(--primary-color);
            }

            .answer-option label {
                display: flex;
                align-items: flex-start;
                gap: 0.5rem;
                padding: 0.5rem;
                cursor: pointer;
                width: 100%;
            }

            .answer-option label:before {
                content: '';
                width: 16px;
                height: 16px;
                flex-shrink: 0;
                margin-top: 4px;
                border: 2px solid var(--text-medium);
                border-radius: 50%;
                display: inline-block;
                transition: all 0.2s ease;
            }

            .answer-option input[type="radio"]:checked+label:before {
                background-color: var(--primary-color);
                box-shadow: inset 0 0 0 3px white;
            }

            @keyframes pulse {
                0% {
                    opacity: 1;
                }

                50% {
                    opacity: 0.7;
                }

                100% {
                    opacity: 1;
                }
            }

            /* Results Section */
            .quiz-results {
                background-color: var(--bg-white);
                border-radius: 10px;
                box-shadow: var(--box-shadow);
                padding: 2rem;
                margin-top: 1.5rem;
                text-align: center;
                border-top: 5px solid var(--primary-color);
            }

            .result-score {
                font-size: 2.5rem;
                font-weight: 700;
                margin: 1.5rem 0;
            }

            .result-pass {
                color: var(--success);
            }

            .result-fail {
                color: var(--danger);
            }

            .result-status-icon {
                font-size: 4.5rem;
                margin-bottom: 1rem;
            }

            .result-message {
                font-size: 1.2rem;
                margin-bottom: 1.5rem;
            }

            /* Question Navigator */
            .question-navigator {
                position: sticky;
                top: 440px;
                padding: 1rem;
                background-color: var(--bg-white);
                border-radius: 10px;
                box-shadow: var(--box-shadow);
                margin-bottom: 1.5rem;
            }

            .navigator-title {
                font-weight: 600;
                margin-bottom: 1rem;
                color: var(--text-dark);
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .question-indicators {
                display: flex;
                flex-wrap: wrap;
                gap: 0.5rem;
            }

            .question-indicator {
                width: 35px;
                height: 35px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 6px;
                background-color: var(--bg-light);
                border: 1px solid var(--text-light);
                cursor: pointer;
                transition: all 0.2s ease;
            }

            .question-indicator:hover {
                background-color: var(--text-light);
            }

            .question-indicator.answered {
                background-color: var(--text-white);
                border-color: var(--success);
                color: var(--success);
            }

            /* Mobile responsiveness */
            @media (max-width: 992px) {
                .quiz-container {
                    flex-direction: column;
                }

                .quiz-info-panel {
                    width: 100%;
                    order: 1;
                }

                .questions-container {
                    order: 2;
                }

                .quiz-header {
                    position: static;
                }
            }
        </style>
    </head>

    <body>
        <div class="quiz-container">
            <!-- Left Column - Quiz Information Panel -->
            <div class="quiz-info-panel">
                <div class="quiz-header">
                    <h2 class="quiz-title">${quiz.title}</h2>
                    <div class="quiz-meta">
                        <div class="quiz-meta-item">
                            <i class="fas fa-question-circle"></i>
                            <span>${quiz.totalQuestions} questions</span>
                        </div>
                        <c:if test="${quiz.timeLimit != null}">
                            <div class="quiz-meta-item">
                                <i class="far fa-clock"></i>
                                <span> 
                                    <fmt:formatNumber value="${Math.floor(quiz.timeLimit / 60)}" pattern="#" />
                                    minutes time limit
                                </span>
                            </div>
                        </c:if>
                        <div class="quiz-meta-item">
                            <i class="fas fa-award"></i>
                            <span>Passing score: ${quiz.passingScore}%</span>
                        </div>
                    </div>

                    <c:if test="${quiz.timeLimit != null}">
                        <div class="timer-container">
                            <div class="timer" id="timer">
                                <i class="far fa-clock mr-1"></i>
                                <span id="timerDisplay">Loading...</span>
                            </div>
                        </div>
                    </c:if>

                    <div class="quiz-progress">
                        <div class="quiz-progress-text">
                            <span id="answeredQuestionsCount">0</span> of ${quiz.totalQuestions} questions
                            answered
                        </div>
                        <div class="progress-bar-container">
                            <div class="progress-bar-fill" id="progressBarFill"></div>
                        </div>
                    </div>

                    <div class="quiz-actions">
                        <button id="submitButton" class="submit-button" disabled>
                            <i class="fas fa-check-circle"></i>
                            Submit Quiz
                        </button>
                    </div>
                </div>

                <!-- Question Navigator -->
                <div class="question-navigator">
                    <div class="navigator-title">
                        <i class="fas fa-map-marker-alt"></i>
                        Question Navigator
                    </div>
                    <div class="question-indicators" id="questionNavigator"></div>
                </div>
            </div>

            <!-- Right Column - Questions List -->
            <div class="questions-container" id="questionsContainer">
                <!-- Questions will be loaded here by JavaScript -->
            </div>

            <!-- Results Container (Initially Hidden) -->
            <div id="resultsContainer" style="display: none;" class="quiz-results">
                <!-- Results will be shown here after submission -->
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Initialize variables
                const quizId = ${ quiz.quizID };
                const quizQuestions = [];
                const userAnswers = {};
                let timeRemaining = ${ quiz.timeLimit != null ? quiz.timeLimit : -1};
                let timerInterval;
                let hasTimeLimit = ${ quiz.timeLimit != null ? true : false};
                // Parse attempt data from server
                let attemptData = '${attempt}';
                let attempt = null;
                if (attemptData && attemptData.trim() !== "") {
                    try {
                     // Use native JSON.parse for proper UTF-8 handling
                        attempt = JSON.parse(attemptData);
                    } catch (e) {
                        console.error("Error parsing attempt data:", e);
                    }
                }
                // Function to safely decode HTML entities
                function decodeHTML(html) {
                    const txt = document.createElement('textarea');
                    txt.innerHTML = html;
                    return txt.value;
                }

                // Load questions data
                <c:forEach items="${quiz.questions}" var="question" varStatus="status">
                quizQuestions.push({
                        questionId: ${question.questionID},
                        content: decodeHTML("${question.content}"),
                        points: ${question.points},
                        orderIndex: ${question.orderIndex},
                        type: "${question.type}",
                        answers: [
                            <c:forEach items="${question.answers}" var="answer" varStatus="answerStatus">
                                {
                                    answerId: ${answer.answerID},
                                    content: decodeHTML("${answer.content}")
                                }   <c:if test="${!answerStatus.last}">,</c:if>
                            </c:forEach>
                        ]
                });
                </c:forEach>
                // Function to initialize the quiz
                async function initQuiz() {
                    try {
                        if (!attempt) {
                            attempt = await startQuizAttempt();
                        }

                        // Load user's previous answers if they exist
                        if (attempt.userAnswers && attempt.userAnswers.length > 0) {
                            attempt.userAnswers.forEach(answer => {
                                userAnswers[answer.questionID] = {
                                    answerId: answer.answerID,
                                    userResponse: answer.userResponse
                                };
                            });
                        }

                        renderQuestions();
                        setupQuestionNavigator();
                        updateProgress();
                        updateSubmitButton();


                        setupEventListeners();
                        if (hasTimeLimit) {
                            startTimer();
                        }
                    } catch (error) {
                        console.error("Error initializing quiz:", error);
                        alert("An error occurred while initializing the quiz. Please try again later.");
                    }
                }

                // Function to start a new quiz attempt
                async function startQuizAttempt() {
                    const response = await fetch('${pageContext.request.contextPath}/api/quiz/start', {
                    method: 'POST',
                        headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: 'quizId=' + quizId
                    });
                    if (!response.ok) {
                        throw new Error('Failed to start quiz attempt');
                    }

                    return await response.json();
                }

                // Function to setup the question navigator
                function setupQuestionNavigator() {
                    const navigatorEl = document.getElementById('questionNavigator');
                    if (!navigatorEl) return;
                    navigatorEl.innerHTML = '';
                    quizQuestions.forEach((question, index) => {
                        const indicator = document.createElement('div');
                        indicator.className = 'question-indicator';
                        indicator.textContent = index + 1;
                        if (userAnswers[question.questionId]) {
                            indicator.classList.add('answered');
                        }

                        indicator.addEventListener('click', () => {
                            document.getElementById('question-' + question.questionId).scrollIntoView({
                                behavior: 'smooth'
                            });
                        });
                        navigatorEl.appendChild(indicator);
                    });
                }

                // Function to render all questions
                function renderQuestions() {
                    const container = document.getElementById('questionsContainer');
                    if (!container) return;
                    container.innerHTML = '';
                    quizQuestions.forEach((question, index) => {
                        const questionCard = document.createElement('div');
                        questionCard.className = 'question-card';
                        questionCard.id = 'question-' + question.questionId;
                        const questionNumber = document.createElement('div');
                        questionNumber.className = 'question-number';
                        const questionNumberText = document.createElement('span');
                        questionNumberText.textContent = 'Question ' + (index + 1);
                        questionNumber.appendChild(questionNumberText);
                        const questionStatus = document.createElement('span');
                        questionStatus.className = 'question-status';
                        questionStatus.id = 'status-' + question.questionId;
                        if (userAnswers[question.questionId]) {
                            questionStatus.textContent = 'Answered';
                            questionStatus.classList.add('status-answered');
                        } else {
                            questionStatus.textContent = 'Not answered';
                        }

                        questionNumber.appendChild(questionStatus);
                        const questionContent = document.createElement('div');
                        questionContent.className = 'question-content';
                        questionContent.textContent = question.content;
                        const answerOptions = document.createElement('div');
                        answerOptions.className = 'answer-options';
                        // Create answer options
                        if (question.answers && question.answers.length > 0) {
                            question.answers.forEach(answer => {
                                const answerOption = document.createElement('div');
                                answerOption.className = 'answer-option';
                                const input = document.createElement('input');
                                input.type = 'radio';
                                input.name = 'question_' + question.questionId;
                                input.id = 'answer-' + question.questionId + '-' + answer.answerId;
                                input.value = answer.answerId;
                                // Check if this answer was previously selected
                                if (userAnswers[question.questionId] && userAnswers[question.questionId].answerId === answer.answerId) {
                                        input.checked = true;
                                }

                                // Add event listener to save answer
                                input.addEventListener('change', function () {
                                    if (this.checked) {
                                        saveAnswer(question.questionId, answer.answerId);
                                    }
                                });
                                const label = document.createElement('label');
                                label.htmlFor = 'answer-' + question.questionId + '-' + answer.answerId;
                                label.textContent = answer.content;
                                answerOption.appendChild(input);
                                answerOption.appendChild(label);
                                answerOptions.appendChild(answerOption);
                            });
                        }

                        questionCard.appendChild(questionNumber);
                        questionCard.appendChild(questionContent);
                        questionCard.appendChild(answerOptions);
                        container.appendChild(questionCard);
                    });
                }

                // Function to save an answer
                async function saveAnswer(questionId, answerId) {
                    // Save locally first
                    userAnswers[questionId] = {
                        answerId: answerId,
                        userResponse: null
                    };
                    // Update status indicator
                    const statusElement = document.getElementById('status-' + questionId);
                    if (statusElement) {
                        statusElement.textContent = 'Answered';
                        statusElement.classList.add('status-answered');
                    }

                    // Then send to server
                    try {
                        const response = await fetch('${pageContext.request.contextPath}/api/quiz/save-answer', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: 'attemptId=' + attempt.attemptID + '&questionId=' + questionId + '&answerId=' + answerId
                        });
                        if (!response.ok) {
                            console.error('Failed to save answer');
                        }

                        // Update UI
                        updateProgress();
                        setupQuestionNavigator();
                        updateSubmitButton();
                    } catch (error) {
                        console.error('Error saving answer:', error);
                    }
                }

                // Function to update progress
                function updateProgress() {
                    const totalAnswered = Object.keys(userAnswers).length;
                    const answeredQuestionsCountEl = document.getElementById('answeredQuestionsCount');
                    if (answeredQuestionsCountEl) {
                        answeredQuestionsCountEl.textContent = totalAnswered;
                    }

                    const progressBarFill = document.getElementById('progressBarFill');
                    if (progressBarFill) {
                        const progressPercent = (totalAnswered / quizQuestions.length) * 100;
                        progressBarFill.style.width = progressPercent + '%';
                    }
                }

                // Function to update submit button state
                function updateSubmitButton() {
                    const submitButton = document.getElementById('submitButton');
                    if (!submitButton) return;
                        const totalAnswered = Object.keys(userAnswers).length;
                        // Enable submit button if all questions are answered
                    submitButton.disabled = totalAnswered < quizQuestions.length;
                }

                // Function to handle the submit quiz action
                async function submitQuiz(action = 'normal') {
                    const totalAnswered = Object.keys(userAnswers).length;
                    // If not all questions are answered, show a warning
                    if (action === 'timeOut') {
                        // Just end the quiz, not allowing confirm
                    } else if(totalAnswered < quizQuestions.length) {
                        if (!confirm('You have not answered all questions. Are you sure you want to submit?')) {
                            return;
                        }
                    } else {
                        if (!confirm('Are you sure you want to submit the quiz? This cannot be undone.')) {
                            return;
                        }
                    }

                    try {
                        const response = await fetch('${pageContext.request.contextPath}/api/quiz/submit', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                            body: 'attemptId=' + attempt.attemptID
                        });
                        if (!response.ok) {
                            throw new Error('Failed to submit quiz');
                        }

                        const result = await response.json();
                        showResults(result);
                        // Stop timer if it's running
                        if (timerInterval) {
                            clearInterval(timerInterval);
                        }
                    } catch (error) {
                        console.error('Error submitting quiz:', error);
                        alert('An error occurred while submitting the quiz. Please try again.');
                    }
                }

                // Function to show quiz results
                function showResults(attempt) {
                console.log(JSON.stringify(attempt));
                    // Hide questions and info panel
                    const questionsContainer = document.getElementById('questionsContainer');
                    const quizInfoPanel = document.querySelector('.quiz-info-panel');
                    if (questionsContainer) questionsContainer.style.display = 'none';
                    if (quizInfoPanel) quizInfoPanel.style.display = 'none';
                    // Show results
                    const resultsContainer = document.getElementById('resultsContainer');
                    if (!resultsContainer) return;
                    resultsContainer.style.display = 'block';
                    resultsContainer.style.width = '100%';
                    const isPassed = attempt.isPassed;
                    const score = attempt.score;
                    const passingScore = ${ quiz.passingScore };
                    let html = '';
                    html += '<div class="result-status-icon">';
                    html += isPassed ?
                            '<i class="fas fa-check-circle text-success"></i>' :
                            '<i class="fas fa-times-circle text-danger"></i>';
                    html += '</div>';
                    html += '<h2>Quiz Results</h2>';
                    html += '<div class="result-score ' + (isPassed ? 'result-pass' : 'result-fail') + '">';
                    html += score + '%';
                    html += '</div>';
                    html += '<div class="result-message">';
                    html += isPassed ?
                            'Congratulations! You passed the quiz.' :
                            'You did not pass. Required score: ' + passingScore + '%';
                    html += '</div>';
                    html += '<div class="mt-4">';
                    html += '<a href="${pageContext.request.contextPath}/learning/quiz/' + quizId + '" ';
                    html += 'class="btn ' + (isPassed ? 'btn-primary' : 'btn-warning') + '">';
                    html += isPassed ? 'Return to Course' : 'Try Again';
                    html += '</a>';
                    html += '</div>';
                    resultsContainer.innerHTML = html;
                }

                // Function to start the timer
                function startTimer() {
                    updateTimerDisplay();
                    timerInterval = setInterval(function () {
                        timeRemaining--;
                        if (timeRemaining <= 0) {
                            clearInterval(timerInterval);
                            submitQuiz('timeOut');
                        }

                        updateTimerDisplay();
                    }, 1000);
                }

                // Function to update timer display
                function updateTimerDisplay() {
                    const timerDisplay = document.getElementById('timerDisplay');
                    const timer = document.getElementById('timer');
                    if (!timerDisplay || !timer) return;
                    const minutes = Math.floor(timeRemaining / 60);
                    const seconds = timeRemaining % 60;
                    timerDisplay.textContent = minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
                    const timeLimitSeconds = ${ quiz.timeLimit != null ? quiz.timeLimit : 0};
                    // Add warning class when less than 20% of time remaining
                    if (timeRemaining < (timeLimitSeconds) * 0.2) {
                        timer.className = 'timer danger';
                    }
                    // Add warning class when less than 50% of time remaining
                    else if (timeRemaining < (timeLimitSeconds) * 0.5) {
                        timer.className = 'timer warning';
                    }
                }

                // Setup event listeners
                function setupEventListeners() {
                    // Submit button
                    const submitButton = document.getElementById('submitButton');
                    if (submitButton) {
                        submitButton.addEventListener('click', submitQuiz);
                    }

                    // Check for time limit expired
                    if (hasTimeLimit && attempt && attempt.startTime) {
                        const startTime = new Date(attempt.startTime).getTime();
                        const now = new Date().getTime();
                        const elapsedTimeSeconds = Math.floor((now - startTime) / 1000);
                        const timeLimitSeconds = ${ quiz.timeLimit != null ? quiz.timeLimit : 0};
                        if (elapsedTimeSeconds >= timeLimitSeconds) {
                            submitQuiz('timeOut');
                        } else {
                            timeRemaining = timeLimitSeconds - elapsedTimeSeconds;
                        }
                    }
                }

                // Initialize the quiz
                initQuiz();
            });
        </script>

        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
    </body>

</html>