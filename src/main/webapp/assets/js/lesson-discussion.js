/**
 * Lesson Discussion Module
 * Modern implementation with collapsible replies
 */
const LessonDiscussion = (function () {
    // DOM elements
    let discussionModal;
    let discussionOverlay;
    let discussionContent;
    let discussionsList;
    let currentLessonId;
    let currentCourseId;
    let currentLessonTitle;
    let discussionForm;
    let discussionContentInput;
    let createDiscussionBtn;
    let cancelDiscussionBtn;
    let closeModalBtn;
    let simplifiedInput;
    let formWrapper;


    /**
     * Initialize the discussion module
     * @param {number} lessonId - The ID of the current lesson
     * @param {number} courseId - The ID of the current course
     * @param {string} lessonTitle - The title of the current lesson
     */
    function init(lessonId, courseId, lessonTitle) {

        currentLessonId = lessonId;
        currentCourseId = courseId;
        currentLessonTitle = lessonTitle || "Discusstion";

        // Create modal elements if they don't exist
        createDiscussionModal();

        // Add click event to the discussion button
        const discussionBtn = document.querySelector(".js-discussion-btn");
        if (discussionBtn) {
            discussionBtn.addEventListener("click", openDiscussionModal);
        }

        // Add click event to the simplified input
        simplifiedInput.addEventListener("click", showFullDiscussionForm);

        // Add submit event to the discussion form
        discussionForm.addEventListener("submit", handleDiscussionFormSubmit);

        // Add event to cancel button
        if (cancelDiscussionBtn) {
            cancelDiscussionBtn.addEventListener("click", hideFullDiscussionForm);
        }

        // Add event delegation for discussions
        discussionsList.addEventListener("click", handleDiscussionActions);

        // Close modal when clicking overlay
        discussionOverlay.addEventListener("click", closeDiscussionModal);

        // Close modal with button
        closeModalBtn.addEventListener("click", closeDiscussionModal);

        // Close modal with Escape key
        document.addEventListener("keydown", function (event) {
            if (
                    event.key === "Escape" &&
                    discussionModal.classList.contains("active")
                    ) {
                closeDiscussionModal();
            }
        });
    }

    /**
     * Create the discussion modal elements
     */
    function createDiscussionModal() {
        // Create elements if they don't exist
        if (!document.getElementById("lesson-discussion-modal")) {
            // Create modal container
            discussionModal = document.createElement("div");
            discussionModal.id = "lesson-discussion-modal";
            discussionModal.className = "lesson-discussion-modal";

            // Create overlay
            discussionOverlay = document.createElement("div");
            discussionOverlay.className = "lesson-discussion-overlay";

            // Create modal content
            discussionContent = document.createElement("div");
            discussionContent.className = "lesson-discussion-content";

            // Create header with lesson title
            const header = document.createElement("div");
            header.className = "lesson-discussion-header";

            const modalTitle = document.createElement("h2");
            modalTitle.textContent = currentLessonTitle || "Discussion Forum";
            modalTitle.className = "lesson-title";

            // Create close button
            closeModalBtn = document.createElement("button");
            closeModalBtn.className = "lesson-discussion-close";
            closeModalBtn.innerHTML = "&times;";

            header.appendChild(modalTitle);
            header.appendChild(closeModalBtn);

            // Create simplified input container
            const simplifiedContainer = document.createElement("div");
            simplifiedContainer.className = "simplified-input-container";

            simplifiedInput = document.createElement("input");
            simplifiedInput.type = "text";
            simplifiedInput.className = "simplified-discussion-input";
            simplifiedInput.placeholder = "Ask a question about this lesson...";

            simplifiedContainer.appendChild(simplifiedInput);

            // Create new discussion form (initially hidden)
            formWrapper = document.createElement("div");
            formWrapper.className = "lesson-discussion-form-wrapper hidden";

            discussionForm = document.createElement("form");
            discussionForm.className = "lesson-discussion-form";

            discussionContentInput = document.createElement("textarea");
            discussionContentInput.name = "content";
            discussionContentInput.placeholder =
                    "Describe your question or discussion topic here...";
            discussionContentInput.required = true;
            discussionContentInput.rows = 4;

            const buttonContainer = document.createElement("div");
            buttonContainer.className = "form-button-container";

            createDiscussionBtn = document.createElement("button");
            createDiscussionBtn.type = "submit";
            createDiscussionBtn.className = "btn btn-primary";
            createDiscussionBtn.textContent = "Post Question";

            cancelDiscussionBtn = document.createElement("button");
            cancelDiscussionBtn.type = "button";
            cancelDiscussionBtn.className = "btn btn-secondary";
            cancelDiscussionBtn.textContent = "Cancel";

            buttonContainer.appendChild(createDiscussionBtn);
            buttonContainer.appendChild(cancelDiscussionBtn);

            discussionForm.appendChild(discussionContentInput);
            discussionForm.appendChild(buttonContainer);

            formWrapper.appendChild(discussionForm);

            // Create discussions list
            discussionsList = document.createElement("div");
            discussionsList.className = "lesson-discussions-list";
            discussionsList.innerHTML =
                    '<div class="loading">Loading discussions...</div>';

            // Assemble modal
            discussionContent.appendChild(header);
            discussionContent.appendChild(simplifiedContainer);
            discussionContent.appendChild(formWrapper);
            discussionContent.appendChild(discussionsList);

            discussionModal.appendChild(discussionOverlay);
            discussionModal.appendChild(discussionContent);

            // Append to body
            document.body.appendChild(discussionModal);
        } else {
            // Get existing elements
            discussionModal = document.getElementById("lesson-discussion-modal");
            discussionOverlay = discussionModal.querySelector(
                    ".lesson-discussion-overlay"
                    );
            discussionContent = discussionModal.querySelector(
                    ".lesson-discussion-content"
                    );
            discussionsList = discussionModal.querySelector(
                    ".lesson-discussions-list"
                    );
            closeModalBtn = discussionModal.querySelector(".lesson-discussion-close");
            discussionForm = discussionModal.querySelector(".lesson-discussion-form");
            formWrapper = discussionModal.querySelector(
                    ".lesson-discussion-form-wrapper"
                    );
            simplifiedInput = discussionModal.querySelector(
                    ".simplified-discussion-input"
                    );
            discussionContentInput = discussionForm.querySelector(
                    'textarea[name="content"]'
                    );
            createDiscussionBtn = discussionForm.querySelector(
                    'button[type="submit"]'
                    );
            cancelDiscussionBtn = discussionForm.querySelector(
                    "button.btn-secondary"
                    );

            // Update lesson title
            const titleElement = discussionModal.querySelector(".lesson-title");
            if (titleElement && currentLessonTitle) {
                titleElement.textContent = currentLessonTitle;
            }
        }
    }

    /**
     * Show the full discussion form
     */
    function showFullDiscussionForm() {
        simplifiedInput.classList.add("hidden");
        formWrapper.classList.remove("hidden");
        discussionContentInput.focus();
        discussionContentInput.value = simplifiedInput.value;
        simplifiedInput.value = "";
    }

    /**
     * Hide the full discussion form and show simplified input
     */
    function hideFullDiscussionForm() {
        formWrapper.classList.add("hidden");
        simplifiedInput.classList.remove("hidden");
        discussionContentInput.value = "";
    }

    /**
     * Open the discussion modal and load discussions
     */
    function openDiscussionModal() {
        // Show the modal with animation
        discussionModal.style.display = "block";
        setTimeout(() => {
            discussionModal.classList.add("active");
        }, 10);

        // Load discussions for the lesson
        loadDiscussions();
    }

    /**
     * Close the discussion modal
     */
    function closeDiscussionModal() {
        discussionModal.classList.remove("active");
        setTimeout(() => {
            discussionModal.style.display = "none";
            // Reset form state
            hideFullDiscussionForm();
        }, 300); // Match transition duration in CSS
    }

    /**
     * Load discussions for the current lesson
     */
    function loadDiscussions() {
        discussionsList.innerHTML =
                '<div class="loading">Loading discussions...</div>';

        fetch(`${window.contextPath}/lesson/discussions/${currentLessonId}`)
                .then((response) => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then((data) => {
                    console.log("Discussions data:", data);
                    if (data.success) {
                        renderDiscussions(data.discussions);
                    } else {
                        throw new Error(data.message || "Failed to load discussions");
                    }
                })
                .catch((error) => {
                    console.error("Error loading discussions:", error);
                    discussionsList.innerHTML = `<div class="error">Error: ${error.message}</div>`;
                });
    }

    /**
     * Render discussions in the modal
     * @param {Array} discussions - List of discussion objects
     */
    function renderDiscussions(discussions) {
        
        if (!discussions || discussions.length === 0) {
            discussionsList.innerHTML =
                    '<div class="no-discussions">No discussions yet. Be the first to ask a question!</div>';
            return;
        }

        let html = "";

        discussions.forEach((discussion) => {
            // Format date for display
            const createdAt = discussion.createdAt
                    ? new Date(discussion.createdAt)
                    : new Date();
            const formattedDate = formatDate(createdAt);

            // Generate HTML for discussion
            html += `
                <div class="discussion-item" data-id="${
                    discussion.id
                    }">
                    <div class="discussion-content">${escapeHtml(
                            discussion.content
                            )}</div>
                    
                    <div class="discussion-meta">
                        <div class="discussion-author">
                            <div class="author-avatar"></div>
                            <span class="author-name">${escapeHtml(
                    discussion.userName
                    )}</span>
                        </div>
                        <div class="discussion-date">${formattedDate}</div>
                    </div>
                    
                    <div class="discussion-replies">
                        <div class="replies-header">
                            <div class="replies-toggle" data-id="${
                    discussion.id
                    }">
                                <span>Replies (${discussion.replyCount})</span>
                                <i class="fa fa-chevron-down"></i>
                            </div>
                            <button class="reply-button btn-link" data-discussion-id="${
                    discussion.id
                    }">Reply</button>
                        </div>
                        
                        <div class="replies-list" id="replies-${
                    discussion.id
                    }">
                            ${renderReplies(discussion.replies || [])}
                        </div>
                        
                        <div class="reply-form-wrapper hidden" id="reply-form-${
                    discussion.id
                    }">
                            <form class="reply-form" data-discussion-id="${
                    discussion.id
                    }">
                                <textarea name="content" placeholder="Write your reply..." required></textarea>
                                <div class="form-button-container">
                                    <button type="submit" class="btn btn-sm btn-primary">Post Reply</button>
                                    <button type="button" class="btn btn-sm btn-secondary cancel-reply" data-discussion-id="${
                    discussion.id
                    }">Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            `;
        });

        discussionsList.innerHTML = html;

        // Add event listeners to reply forms
        const replyForms = document.querySelectorAll(".reply-form");
        replyForms.forEach((form) => {
            form.addEventListener("submit", handleReplyFormSubmit);
        });

        // Add event listeners to toggle replies
        const toggleButtons = document.querySelectorAll(".replies-toggle");
        toggleButtons.forEach((button) => {
            button.addEventListener("click", toggleReplies);
        });

        // Add event listeners to reply buttons
        const replyButtons = document.querySelectorAll(".reply-button");
        replyButtons.forEach((button) => {
            button.addEventListener("click", showReplyForm);
        });

        // Add event listeners to cancel reply buttons
        const cancelReplyButtons = document.querySelectorAll(".cancel-reply");
        cancelReplyButtons.forEach((button) => {
            button.addEventListener("click", hideReplyForm);
        });
    }

    /**
     * Show reply form for a discussion
     * @param {Event} event - Click event
     */
    function showReplyForm(event) {
        const discussionId = event.currentTarget.getAttribute("data-discussion-id");
        const replyForm = document.getElementById(`reply-form-${discussionId}`);

        if (replyForm) {
            replyForm.classList.remove("hidden");
            replyForm.querySelector("textarea").focus();
        }
    }

    /**
     * Hide reply form for a discussion
     * @param {Event} event - Click event
     */
    function hideReplyForm(event) {
        const discussionId = event.currentTarget.getAttribute("data-discussion-id");
        const replyForm = document.getElementById(`reply-form-${discussionId}`);

        if (replyForm) {
            replyForm.classList.add("hidden");
            replyForm.querySelector("textarea").value = "";
        }
    }

    /**
     * Toggle replies visibility
     * @param {Event} event - Click event
     */
    function toggleReplies(event) {
        const discussionId = event.currentTarget.getAttribute("data-id");
        const repliesList = document.getElementById(`replies-${discussionId}`);

        if (repliesList) {
            const isCurrentlyOpen = repliesList.classList.contains("open");

            // Toggle classes
            event.currentTarget.classList.toggle("open", !isCurrentlyOpen);
            repliesList.classList.toggle("open", !isCurrentlyOpen);
        }
    }

    /**
     * Render discussion replies
     * @param {Array} replies - List of reply objects
     * @return {string} HTML for replies
     */
    function renderReplies(replies) {
        if (!replies || replies.length === 0) {
            return '<div class="no-replies">No replies yet</div>';
        }

        let html = "";

        replies.forEach((reply) => {
            // Format date for display
            const createdAt = reply.createdAt
                    ? new Date(reply.createdAt)
                    : new Date();
            const formattedDate = formatDate(createdAt);

            // CSS classes for styling
            const replyClasses = [
                "reply-item",
                reply.isInstructorReply ? "instructor-reply" : "",
                reply.isAcceptedAnswer ? "accepted-answer" : ""
            ]
                    .filter(Boolean)
                    .join(" ");

            html += `
                <div class="${replyClasses}">
                    <div class="reply-content">${escapeHtml(
                    reply.content
                    )}</div>
                    
                    <div class="reply-meta">
                        <div class="reply-author">
                            <span class="author-name">${escapeHtml(
                    reply.userName
                    )}</span>
                            ${
                    reply.isInstructorReply
                    ? '<span class="instructor-badge">Instructor</span>'
                    : ""
                    }
                        </div>
                        <div class="reply-date">${formattedDate}</div>
                    </div>
                </div>
            `;
        });

        return html;
    }

    /**
     * Format date for display
     * @param {Date} date - Date to format
     * @return {string} Formatted date string
     */
    function formatDate(date) {
        // Check if the date is today
        const today = new Date();
        const isToday =
                date.getDate() === today.getDate() &&
                date.getMonth() === today.getMonth() &&
                date.getFullYear() === today.getFullYear();

        if (isToday) {
            const hours = date.getHours().toString().padStart(2, "0");
            const minutes = date.getMinutes().toString().padStart(2, "0");
            return `Today at ${hours}:${minutes}`;
        } else {
            // Format as day/month/year
            return (
                    date.toLocaleDateString() +
                    " " +
                    date.getHours().toString().padStart(2, "0") +
                    ":" +
                    date.getMinutes().toString().padStart(2, "0")
                    );
        }
    }

    /**
     * Handle discussion form submit
     * @param {Event} event - The form submit event
     */
    function handleDiscussionFormSubmit(event) {
        event.preventDefault();

        const content = discussionContentInput.value.trim();

        if (!content) {
            alert("Please enter your question");
            return;
        }

        // Disable the submit button
        createDiscussionBtn.disabled = true;
        createDiscussionBtn.textContent = "Posting...";

        // Create form data
        const formData = new FormData();
        formData.append("lessonId", currentLessonId);
        formData.append("courseId", currentCourseId);
        formData.append("content", content);

        console.log("Submitting discussion:", {
            lessonId: currentLessonId,
            courseId: currentCourseId,
            content: content
        });

        // Send the request
        fetch(`${window.contextPath}/lesson/discussion/create?lessonId=${currentLessonId}&courseId=${currentCourseId}&content=${content}`, {
            method: "POST"
        })
                .then((response) => {
                    console.log("Create discussion response status:", response.status);
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then((data) => {
                    console.log("Create discussion response:", data);
                    if (data.success) {
                        // Clear form and hide it
                        discussionContentInput.value = "";
                        hideFullDiscussionForm();

                        // Show success message briefly
                        showToast("Question posted successfully!");

                        // Reload discussions
                        loadDiscussions();

                        // Scroll to top of discussions list
                        discussionsList.scrollTop = 0;
                    } else {
                        throw new Error(data.message || "Failed to create discussion");
                    }
                })
                .catch((error) => {
                    console.error("Error creating discussion:", error);
                    showToast(`Error: ${error.message}`, "error");
                })
                .finally(() => {
                    // Re-enable the submit button
                    createDiscussionBtn.disabled = false;
                    createDiscussionBtn.textContent = "Post Question";
                });
    }

    /**
     * Handle reply form submit
     * @param {Event} event - The form submit event
     */
    function handleReplyFormSubmit(event) {
        event.preventDefault();

        const form = event.target;
        const discussionId = form.getAttribute("data-discussion-id");
        const content = form.querySelector("textarea").value.trim();
        const submitBtn = form.querySelector('button[type="submit"]');

        if (!content) {
            alert("Please enter a reply");
            return;
        }

        // Disable the submit button
        submitBtn.disabled = true;
        submitBtn.textContent = "Sending...";

        // Create form data
        const formData = new FormData();
        formData.append("discussionId", discussionId);
        formData.append("content", content);

        console.log("Submitting reply:", {
            discussionId: discussionId,
            content: content
        });

        // Send the request
        fetch(`${window.contextPath}/lesson/discussion/reply?discussionId=${discussionId}&content=${content}`, {
            method: "POST"
        })
                .then((response) => {
                    console.log("Create reply response status:", response.status);
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then((data) => {
                    console.log("Create reply response:", data);
                    if (data.success) {
                        // Clear form and hide it
                        form.querySelector("textarea").value = "";
                        hideReplyForm({currentTarget: form.querySelector(".cancel-reply")});

                        // Show success message
                        showToast("Reply posted successfully!");

                        // Make sure the reply list is visible
                        const repliesList = document.getElementById(
                                `replies-${discussionId}`
                                );
                        const repliesToggle = document.querySelector(
                                `.replies-toggle[data-id="${discussionId}"]`
                                );
                        if (repliesList && repliesToggle) {
                            repliesList.classList.add("open");
                            repliesToggle.classList.add("open");
                        }

                        // Reload discussions to refresh all content
                        loadDiscussions();
                    } else {
                        throw new Error(data.message || "Failed to add reply");
                    }
                })
                .catch((error) => {
                    console.error("Error creating reply:", error);
                    showToast(`Error: ${error.message}`, "error");
                })
                .finally(() => {
                    // Re-enable the submit button
                    submitBtn.disabled = false;
                    submitBtn.textContent = "Post Reply";
                });
    }

    /**
     * Handle click events on discussions
     * @param {Event} event - The click event
     */
    function handleDiscussionActions(event) {
        // Future functionality for marking as resolved, etc.
    }

    /**
     * Show a toast notification
     * @param {string} message - Message to display
     * @param {string} type - Type of toast (success, error)
     */
    function showToast(message, type = "success") {
        const toast = document.createElement("div");
        toast.className = `discussion-toast ${type}`;
        toast.textContent = message;
        document.body.appendChild(toast);

        // Show toast
        setTimeout(() => {
            toast.classList.add("show");
        }, 10);

        // Hide and remove toast
        setTimeout(() => {
            toast.classList.remove("show");
            setTimeout(() => {
                document.body.removeChild(toast);
            }, 300);
        }, 3000);
    }

    /**
     * Helper function to escape HTML
     * @param {string} unsafe - Unsafe string that might contain HTML
     * @return {string} Escaped HTML string
     */
    function escapeHtml(unsafe) {
        if (!unsafe)
            return "";
        return unsafe
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
    }

    // Public API
    return {
        init: init,
        openModal: openDiscussionModal,
        closeModal: closeDiscussionModal
    };
})();
