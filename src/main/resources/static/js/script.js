document.addEventListener("DOMContentLoaded", function () {
    let alert = document.querySelector(".alert");
    if (alert) {
        setTimeout(() => {
            alert.style.display = "none";
        }, 4000);
    }
});