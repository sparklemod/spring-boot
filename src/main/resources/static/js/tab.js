const adminList = '/admin/list';
const userList = '/user';
const userSection = 'nav-user';
const adminSection = 'nav-admin';

function loadTabContent(url, targetId) {
    fetch(url)
        .then(response => response.text())
        .then(html => {
            document.getElementById(targetId).innerHTML = html;
        })
        .catch(error => console.error('Ошибка загрузки:', error));

    document.querySelectorAll(".tab-content").forEach(tab => {
        tab.style.display = "none";
    });
    document.querySelectorAll(".tablink").forEach(button => {
        button.classList.remove("active");
    });


    document.getElementById(targetId).style.display = "block";
    document.querySelector(`[data-target="${targetId}"]`).className += " active";
}

document.addEventListener("DOMContentLoaded", function () {
    let adminTab = document.getElementById("admin-btn");
    let userTab = document.getElementById("user-btn");

    if (adminTab !== null) {
        loadSection(adminSection)
    } else if (userTab !== null) {
        loadSection(userSection)
    }
});

function loadSection(sectionTargetId) {
    disableSidebarSectionsAndButtons();
    enableContentAndButtonByContentSectionId(sectionTargetId);

    if (sectionTargetId === adminSection) {
        document.querySelectorAll(".tablink").forEach(button => {
            button.classList.remove("active");
        });

        loadTabContent(adminList, 'list-content');
    } else if (sectionTargetId === userSection) {
        loadTabContent(userList, 'user-content');
    }
}

function enableContentAndButtonByContentSectionId(sectionTargetId) {
    document.querySelector(`[data-target="${sectionTargetId}"]`).className += " active";
    let sectionContent = document.getElementById(sectionTargetId);
    sectionContent.style.display = "block";
    sectionContent.className += " active";
}

function disableSidebarSectionsAndButtons() {
    document.querySelectorAll(".sidebar-section-btn").forEach(button => {
        button.classList.remove("active");
    });

    document.querySelectorAll(".section-content").forEach(section => {
        section.style.display = "none";
        section.classList.remove("active");
    });
}