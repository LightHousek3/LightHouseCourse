<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Modal -->
<div id="toggle-modal" class="d-none toggle-modal"></div>

<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const toggleBtn = document.getElementById("toggleSidebarBtn");
    const closeBtn = document.getElementById("closeSidebarBtn");
    const sidebar = document.querySelector(".instructor-sidebar");
    const modal = document.getElementById("toggle-modal");
    if (toggleBtn) {
        toggleBtn.addEventListener("click", () => {
            sidebar.classList.toggle("show");
            modal.classList.toggle("show");
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            sidebar.classList.remove("show");
            modal.classList.remove("show");
        });
    }

    if (modal) {
        modal.addEventListener("click", () => {
            sidebar.classList.remove("show");
            modal.classList.remove("show");
        });

        window.addEventListener('resize', () => {
            if (window.innerWidth >= 992) {
                modal.classList.remove("show");
                sidebar.classList.remove("show");
            } else if (sidebar.classList.contains("show")) {
                modal.classList.add("show");
            }
        });

    }
</script>