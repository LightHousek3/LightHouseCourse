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
   * Render discussions list
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

      html += `
                <div class="discussion-item" data-id="${discussion.id}">
                    <div class="discussion-content">${escapeHtml(
                      discussion.content
                    )}</div>
                    
                    <div class="discussion-meta">
                        <div>
                            <div class="discussion-author">
                                <div class="author-avatar">
                                    <img src=${window.contextPath}${discussion.authorAvatar} alt="avatar" />
                                </div>
                                <span class="author-name">${escapeHtml(
                                  discussion.authorName
                                )}</span>
                            </div>
                            <div class="discussion-date">${formattedDate}</div>
                        </div>
                        <div class="discussion-actions">
                            ${
                              discussion.isAuthor
                                ? `
                                <button class="btn-link edit-discussion-btn" data-id="${discussion.id}">
                                    <i class="fa fa-edit"></i> Edit
                                </button>
                                <button class="btn-link delete-discussion-btn" data-id="${discussion.id}">
                                    <i class="fa fa-trash"></i> Delete
                                </button>
                            `
                                : ""
                            }
                        </div>
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
                        
                        <div class="replies-list" id="replies-${discussion.id}">
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

    // Add event listeners to edit discussion buttons
    const editDiscussionButtons = document.querySelectorAll(
      ".edit-discussion-btn"
    );
    editDiscussionButtons.forEach((button) => {
      button.addEventListener("click", handleEditDiscussion);
    });

    // Add event listeners to delete discussion buttons
    const deleteDiscussionButtons = document.querySelectorAll(
      ".delete-discussion-btn"
    );
    deleteDiscussionButtons.forEach((button) => {
      button.addEventListener("click", handleDeleteDiscussion);
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
        reply.isAcceptedAnswer ? "accepted-answer" : "",
      ]
        .filter(Boolean)
        .join(" ");

      html += `
                <div class="${replyClasses}" data-id="${reply.id}">
                    <div class="reply-content">${escapeHtml(
                      reply.content
                    )}</div>
                    
                    <div class="reply-meta">
                        <div class="d-flex flex-column">
                            <div class="reply-author">
                            <img src=${window.contextPath}${reply.authorAvatar} alt="avatar" />
                            <span class="author-name">${escapeHtml(
                              reply.authorName
                            )}</span>
                            ${
                              reply.isInstructorReply
                                ? '<span class="instructor-badge">Instructor</span>'
                                : ""
                            }
                        </div>
                        <div class="reply-date">${formattedDate}</div>
                        </div>
                        <div class="reply-actions">
                            ${
                              reply.isAuthor
                                ? `
                                <button class="btn-link edit-reply-btn" data-id="${reply.id}">
                                    <i class="fa fa-edit"></i> Edit
                                </button>
                                <button class="btn-link delete-reply-btn" data-id="${reply.id}">
                                    <i class="fa fa-trash"></i> Delete
                                </button>
                            `
                                : ""
                            }
                        </div>
                    </div>
                </div>
            `;
    });

    return html;
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
      content: content,
    });

    // Send the request
    fetch(
      `${window.contextPath}/lesson/discussion/create?lessonId=${currentLessonId}&courseId=${currentCourseId}&content=${content}`,
      {
        method: "POST",
      }
    )
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
      content: content,
    });

    // Send the request
    fetch(
      `${window.contextPath}/lesson/discussion/reply?discussionId=${discussionId}&content=${content}`,
      {
        method: "POST",
      }
    )
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
          hideReplyForm({ currentTarget: form.querySelector(".cancel-reply") });

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
    // Check if edit reply button was clicked
    if (event.target.closest(".edit-reply-btn")) {
      const button = event.target.closest(".edit-reply-btn");
      const replyId = button.getAttribute("data-id");
      handleEditReply(replyId);
      return;
    }

    // Check if delete reply button was clicked
    if (event.target.closest(".delete-reply-btn")) {
      const button = event.target.closest(".delete-reply-btn");
      const replyId = button.getAttribute("data-id");
      handleDeleteReply(replyId);
      return;
    }
  }

  /**
   * Handle editing a discussion
   * @param {Event} event - The click event
   */
  function handleEditDiscussion(event) {
    const discussionId = event.currentTarget.getAttribute("data-id");
    const discussionItem = document.querySelector(
      `.discussion-item[data-id="${discussionId}"]`
    );
    const contentElement = discussionItem.querySelector(".discussion-content");
    const currentContent = contentElement.innerText;

    // Hide action buttons while editing
    const actionsElement = discussionItem.querySelector(".discussion-actions");
    if (actionsElement) {
      actionsElement.style.display = "none";
    }

    // Create edit form
    const editForm = document.createElement("form");
    editForm.className = "edit-discussion-form";
    editForm.innerHTML = `
            <textarea class="edit-content" required>${currentContent}</textarea>
            <div class="form-button-container">
                <button type="submit" class="btn btn-sm btn-primary">Save</button>
                <button type="button" class="btn btn-sm btn-secondary cancel-edit">Cancel</button>
            </div>
        `;

    // Replace content with form
    contentElement.style.display = "none";
    discussionItem.insertBefore(editForm, contentElement.nextSibling);

    // Focus on textarea
    const textarea = editForm.querySelector("textarea");
    textarea.focus();
    textarea.setSelectionRange(textarea.value.length, textarea.value.length);

    // Add event listeners
    editForm.addEventListener("submit", function (e) {
      e.preventDefault();
      const newContent = textarea.value.trim();

      if (!newContent) {
        alert("Content cannot be empty");
        return;
      }

      // Update discussion
      updateDiscussion(discussionId, newContent, function () {
        // Success callback
        contentElement.innerText = newContent;
        contentElement.style.display = "";
        editForm.remove();

        // Show action buttons again
        if (actionsElement) {
          actionsElement.style.display = "";
        }
      });
    });

    editForm
      .querySelector(".cancel-edit")
      .addEventListener("click", function () {
        contentElement.style.display = "";
        editForm.remove();

        // Show action buttons again
        if (actionsElement) {
          actionsElement.style.display = "";
        }
      });
  }

  /**
   * Update a discussion
   * @param {number} discussionId - The ID of the discussion
   * @param {string} content - The new content
   * @param {Function} callback - Callback function on success
   */
  function updateDiscussion(discussionId, content, callback) {
    fetch(
      `${
        window.contextPath
      }/lesson/discussion/update?discussionId=${discussionId}&content=${encodeURIComponent(
        content
      )}`,
      {
        method: "POST",
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (data.success) {
          showToast("Discussion updated successfully");
          if (callback) callback();
        } else {
          throw new Error(data.message || "Failed to update discussion");
        }
      })
      .catch((error) => {
        console.error("Error updating discussion:", error);
        showToast(`Error: ${error.message}`, "error");
      });
  }

  /**
   * Handle deleting a discussion
   * @param {Event} event - The click event
   */
  function handleDeleteDiscussion(event) {
    const discussionId = event.currentTarget.getAttribute("data-id");
    const discussionItem = document.querySelector(
      `.discussion-item[data-id="${discussionId}"]`
    );

    // Get discussion content for the confirmation message
    const content = discussionItem.querySelector(
      ".discussion-content"
    ).innerText;
    const shortContent =
      content.length > 50 ? content.substring(0, 50) + "..." : content;

    showDeleteConfirmModal(
      `Are you sure you want to delete this discussion? "${shortContent}" This action cannot be undone.`,
      () => deleteDiscussion(discussionId)
    );
  }

  /**
   * Delete a discussion
   * @param {number} discussionId - The ID of the discussion
   */
  function deleteDiscussion(discussionId) {
    fetch(
      `${window.contextPath}/lesson/discussion/delete?discussionId=${discussionId}`,
      {
        method: "POST",
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (data.success) {
          showToast("Discussion deleted successfully");
          // Remove the discussion from the DOM
          const discussionItem = document.querySelector(
            `.discussion-item[data-id="${discussionId}"]`
          );
          if (discussionItem) {
            discussionItem.remove();
          }
          // If no discussions left, show message
          if (discussionsList.children.length === 0) {
            discussionsList.innerHTML =
              '<div class="no-discussions">No discussions yet. Be the first to ask a question!</div>';
          }
        } else {
          throw new Error(data.message || "Failed to delete discussion");
        }
      })
      .catch((error) => {
        console.error("Error deleting discussion:", error);
        showToast(`Error: ${error.message}`, "error");
      });
  }

  /**
   * Handle editing a reply
   * @param {number} replyId - The ID of the reply
   */
  function handleEditReply(replyId) {
    const replyItem = document.querySelector(
      `.reply-item[data-id="${replyId}"]`
    );
    const contentElement = replyItem.querySelector(".reply-content");
    const currentContent = contentElement.innerText;

    // Hide action buttons while editing
    const actionsElement = replyItem.querySelector(".reply-actions");
    if (actionsElement) {
      actionsElement.style.display = "none";
    }

    // Create edit form
    const editForm = document.createElement("form");
    editForm.className = "edit-reply-form";
    editForm.innerHTML = `
            <textarea class="edit-content" required>${currentContent}</textarea>
            <div class="form-button-container">
                <button type="submit" class="btn btn-sm btn-primary">Save</button>
                <button type="button" class="btn btn-sm btn-secondary cancel-edit">Cancel</button>
            </div>
        `;

    // Replace content with form
    contentElement.style.display = "none";
    replyItem.insertBefore(editForm, contentElement.nextSibling);

    // Focus on textarea
    const textarea = editForm.querySelector("textarea");
    textarea.focus();
    textarea.setSelectionRange(textarea.value.length, textarea.value.length);

    // Add event listeners
    editForm.addEventListener("submit", function (e) {
      e.preventDefault();
      const newContent = textarea.value.trim();

      if (!newContent) {
        alert("Content cannot be empty");
        return;
      }

      // Update reply
      updateReply(replyId, newContent, function () {
        // Success callback
        contentElement.innerText = newContent;
        contentElement.style.display = "";
        editForm.remove();

        // Show action buttons again
        if (actionsElement) {
          actionsElement.style.display = "";
        }
      });
    });

    editForm
      .querySelector(".cancel-edit")
      .addEventListener("click", function () {
        contentElement.style.display = "";
        editForm.remove();

        // Show action buttons again
        if (actionsElement) {
          actionsElement.style.display = "";
        }
      });
  }

  /**
   * Update a reply
   * @param {number} replyId - The ID of the reply
   * @param {string} content - The new content
   * @param {Function} callback - Callback function on success
   */
  function updateReply(replyId, content, callback) {
    fetch(
      `${
        window.contextPath
      }/lesson/discussion/reply/update?replyId=${replyId}&content=${encodeURIComponent(
        content
      )}`,
      {
        method: "POST",
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (data.success) {
          showToast("Reply updated successfully");
          if (callback) callback();
        } else {
          throw new Error(data.message || "Failed to update reply");
        }
      })
      .catch((error) => {
        console.error("Error updating reply:", error);
        showToast(`Error: ${error.message}`, "error");
      });
  }

  /**
   * Handle deleting a reply
   * @param {number} replyId - The ID of the reply
   */
  function handleDeleteReply(replyId) {
    const replyItem = document.querySelector(
      `.reply-item[data-id="${replyId}"]`
    );

    // Get reply content for the confirmation message
    const content = replyItem.querySelector(".reply-content").innerText;
    const shortContent =
      content.length > 50 ? content.substring(0, 50) + "..." : content;

    showDeleteConfirmModal(
      `Are you sure you want to delete this reply? "${shortContent}" This action cannot be undone.`,
      () => deleteReply(replyId)
    );
  }

  /**
   * Delete a reply
   * @param {number} replyId - The ID of the reply
   */
  function deleteReply(replyId) {
    fetch(
      `${window.contextPath}/lesson/discussion/reply/delete?replyId=${replyId}`,
      {
        method: "POST",
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (data.success) {
          showToast("Reply deleted successfully");
          // Remove the reply from the DOM
          const replyItem = document.querySelector(
            `.reply-item[data-id="${replyId}"]`
          );
          if (replyItem) {
            const repliesList = replyItem.closest(".replies-list");
            replyItem.remove();

            // Update reply count
            const discussionId = repliesList.id.replace("replies-", "");
            const repliesToggle = document.querySelector(
              `.replies-toggle[data-id="${discussionId}"]`
            );
            if (repliesToggle) {
              const countSpan = repliesToggle.querySelector("span");
              const currentCount =
                parseInt(countSpan.textContent.match(/\d+/)[0]) - 1;
              countSpan.textContent = `Replies (${currentCount})`;
            }

            // If no replies left, show message
            if (repliesList.children.length === 0) {
              repliesList.innerHTML =
                '<div class="no-replies">No replies yet</div>';
            }
          }
        } else {
          throw new Error(data.message || "Failed to delete reply");
        }
      })
      .catch((error) => {
        console.error("Error deleting reply:", error);
        showToast(`Error: ${error.message}`, "error");
      });
  }

  /**
   * Format date for display
   * @param {Date} date - The date to format
   * @return {string} Formatted date string
   */
  function formatDate(date) {
    const now = new Date();
    const diffMs = now - date;
    const diffSecs = Math.floor(diffMs / 1000);
    const diffMins = Math.floor(diffSecs / 60);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffSecs < 60) {
      return "just now";
    } else if (diffMins < 60) {
      return `${diffMins} minute${diffMins > 1 ? "s" : ""} ago`;
    } else if (diffHours < 24) {
      return `${diffHours} hour${diffHours > 1 ? "s" : ""} ago`;
    } else if (diffDays < 7) {
      return `${diffDays} day${diffDays > 1 ? "s" : ""} ago`;
    } else {
      return date.toLocaleDateString();
    }
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
    if (!unsafe) return "";
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  /**
   * Create the delete confirmation modal
   * @returns {HTMLElement} The modal element
   */
  function createDeleteConfirmModal() {
    // Check if modal already exists
    let modal = document.getElementById("delete-confirm-modal");
    if (modal) {
      return modal;
    }

    // Create modal container
    modal = document.createElement("div");
    modal.id = "delete-confirm-modal";
    modal.className = "delete-confirm-modal";

    // Create modal dialog
    const dialog = document.createElement("div");
    dialog.className = "delete-confirm-dialog";

    // Create modal header
    const header = document.createElement("div");
    header.className = "delete-confirm-header";

    const title = document.createElement("h4");
    title.innerHTML =
      '<i class="fa fa-exclamation-triangle"></i> Confirm Deletion';

    const closeBtn = document.createElement("button");
    closeBtn.className = "close-btn";
    closeBtn.innerHTML = "&times;";
    closeBtn.addEventListener("click", () => hideDeleteConfirmModal());

    header.appendChild(title);
    header.appendChild(closeBtn);

    // Create modal body
    const body = document.createElement("div");
    body.className = "delete-confirm-body";

    const message = document.createElement("p");
    message.id = "delete-confirm-message";
    message.textContent =
      "Are you sure you want to delete this item? This action cannot be undone.";

    body.appendChild(message);

    // Create modal footer
    const footer = document.createElement("div");
    footer.className = "delete-confirm-footer";

    const cancelBtn = document.createElement("button");
    cancelBtn.className = "btn btn-secondary";
    cancelBtn.textContent = "Cancel";
    cancelBtn.addEventListener("click", () => hideDeleteConfirmModal());

    const deleteBtn = document.createElement("button");
    deleteBtn.className = "btn btn-danger";
    deleteBtn.textContent = "Delete";
    deleteBtn.id = "confirm-delete-btn";

    footer.appendChild(cancelBtn);
    footer.appendChild(deleteBtn);

    // Assemble modal
    dialog.appendChild(header);
    dialog.appendChild(body);
    dialog.appendChild(footer);
    modal.appendChild(dialog);

    // Add to document
    document.body.appendChild(modal);

    // Close modal when clicking outside
    modal.addEventListener("click", (e) => {
      if (e.target === modal) {
        hideDeleteConfirmModal();
      }
    });

    return modal;
  }

  /**
   * Show the delete confirmation modal
   * @param {string} message - The confirmation message
   * @param {Function} onConfirm - Callback function when deletion is confirmed
   */
  function showDeleteConfirmModal(message, onConfirm) {
    const modal = createDeleteConfirmModal();

    // Set message
    document.getElementById("delete-confirm-message").textContent = message;

    // Set confirm action
    const deleteBtn = document.getElementById("confirm-delete-btn");

    // Remove previous event listeners
    const newDeleteBtn = deleteBtn.cloneNode(true);
    deleteBtn.parentNode.replaceChild(newDeleteBtn, deleteBtn);

    // Add new event listener
    newDeleteBtn.addEventListener("click", () => {
      onConfirm();
      hideDeleteConfirmModal();
    });

    // Show modal with animation
    modal.style.display = "flex";
    setTimeout(() => {
      modal.classList.add("show");
    }, 10);
  }

  /**
   * Hide the delete confirmation modal
   */
  function hideDeleteConfirmModal() {
    const modal = document.getElementById("delete-confirm-modal");
    if (modal) {
      modal.classList.remove("show");
      setTimeout(() => {
        modal.style.display = "none";
      }, 300);
    }
  }

  // Public API
  return {
    init: init,
    openModal: openDiscussionModal,
    closeModal: closeDiscussionModal,
  };
})();
