$( '#multiple-select-field' ).select2( {
    theme: "bootstrap-5",
    width: $( this ).data( 'width' ) ? $( this ).data( 'width' ) : $( this ).hasClass( 'w-100' ) ? '100%' : 'style',
    placeholder: $( this ).data( 'placeholder' ),
    closeOnSelect: false,
} );

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