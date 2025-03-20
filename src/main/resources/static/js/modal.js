function openEditModal(userId) {
    document.getElementById("editModal").style.display = "flex";

    fetch(`/admin/edit/${userId}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('editUserForm').innerHTML = html;
        })
        .catch(error => console.error('Ошибка загрузки:', error));
}

window.onclick = function (event) {
    let modal = document.getElementById("editModal");
    if (event.target === modal) {
        closeEditModal();
    }
};

function closeEditModal() {
    document.getElementById("editModal").style.display = "none";
}