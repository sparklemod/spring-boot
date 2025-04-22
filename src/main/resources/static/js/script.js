$(document).ready(function(){
    const HOST = 'http://localhost:8080';

    // Header user info
    apiRequest(HOST + '/api/get-current-user-info')
        .then(data => {
            $('#header-username').text(data.username);
            $('#header-roles').text(data.roles);

            const isAdmin = data.roles.search(/\bADMIN\b/) >= 0;

            if (isAdmin) {
                fetch(HOST + '/api/get-users', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => response.json())
                    .then(users => {
                        users.forEach(userData => {
                            addUserToUserList(userData);
                        });
                    })
                    .catch(error => {
                        console.log("Ошибка при получении пользователей:", error);
                        errorMessage(error.message);
                    });
            } else {
                $('#nav-admin').remove();
                $('#nav-user').addClass('active-section');
                $('#admin-btn').remove();
                $('#user-btn').addClass('active');
            }

            $('#user-info').html(data.username + ' with roles ' + data.roles);
        });

    // Switch admin panel Tabs
    $('#userTabs button').click(function() {
        $('#nav-admin .active-tab').removeClass('active-tab');

        var target = $(this).data('target');

        $('#' + target).addClass('active-tab');

        if (target == 'user-add-content') {
            getOptionRoles($('#user-add-content #form-select-roles'));
        } else if (target == 'all-users-list-content') {
            apiRequest(HOST + '/api/get-users')
                .then(data => {
                });
        }
    });

    // Switch admin tab
    $('#userTabs button').click(function(e) {
        $('#userTabs .active').removeClass('active');
        $(this).addClass('active');
    });

    //Switch Sections
    $('#v-pills-tab button').click(function(e) {
        $('#v-pills-tab .active').removeClass('active');
        $(this).addClass('active');
        $('.section-content-div .active-section').removeClass('active-section');

        var target = $(this).data('target');
        $('#' + target).addClass('active-section');
    });

    // About User
    $.ajax({
        type: "GET",
        url: HOST + '/api/get-current-user-info',
        dataType: 'json',
        contentType: 'application/json',
        success: function(data) {
            $('#nav-user #user-id').text(data.id);
            $('#nav-user #user-name').text(data.name);
            $('#nav-user #user-surname').text(data.surname);
            $('#nav-user #user-email').text(data.username);
            $('#nav-user #user-roles').text(data.roles);
        },
        error: function(xhr) {
            errorMessage(xhr.responseText)
        }
    });

    // Create User
    $('#create-user').on("click", function(e){
        e.preventDefault();
        addUser();
    })

    function getOptionRoles(element) {
        $.ajax({
            type: "GET",
            url: HOST + '/api/get-roles',
            dataType: 'json',
            contentType: 'application/json',
            success: function(roles) {
                roles.forEach(role => {
                    var option = '<option value="' + role.id + '">' + role.name + '</option>';
                    element.append(option);
                });
            },
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
    }

    // Edit User (Open Modal)
    $('#all-users-list-content').delegate('#edit-user button', 'click', function() {
        const userId = $(this).closest('tr').attr('id');
        openUserModal(userId, 'edit');
    });

    // Delete User (Open Modal)
    $('#all-users-list-content').delegate('#delete-user button', 'click', function() {
        const userId = $(this).closest('tr').attr('id');
        openUserModal(userId, 'delete');
    });

    // Edit or Delete User (request)
    $('#editModal #edit-user').click(async function () {
        const action = $('#editModal').attr('data-action');
        const userId = $('#editModal #id').val();

        if (action === 'delete') {
            const response = await apiRequest(HOST + '/api/delete-user/' + userId, {
                method: 'DELETE',
            });

            if (!response) return;
            $('#editModal').modal('hide');
            $('#nav-admin #users-list-table tbody #' + userId).remove();
        } else if (action === 'edit') {
            const data = {
                name: $('#editModal #name').val(),
                surname: $('#editModal #surname').val(),
                username: $('#editModal #email').val(),
                password: $('#editModal #password').val(),
                roleIds: $('#editModal #form-select-roles').val()
            };

            const userData = await apiRequest(HOST + '/api/edit-user/' + userId, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!userData) return;

            $('#editModal').modal('hide');

            const parent = $('#nav-admin #users-list-table tbody #' + userId);
            parent.find('#user-name').text(userData.name);
            parent.find('#user-surname').text(userData.surname);
            parent.find('#user-email').text(userData.username);
            parent.find('#user-roles').text(userData.roles);
        }
    });

    async function openUserModal(userId, action) {
        $('#editModal').attr('data-action', action);
        const isDelete = action === 'delete';

        $('#editModal .modal-title').text(isDelete ? 'Delete User' : 'Edit User');
        $('#editModal input, #editModal select').prop('disabled', isDelete);
        $('#editModal #edit-user')
            .text(isDelete ? 'Delete' : 'Edit')
            .toggleClass('btn-danger', isDelete)
            .toggleClass('btn-primary', !isDelete);

        getOptionRoles($('#editModal #form-select-roles'));

        const userData = await apiRequest(HOST + '/api/get-user/' + userId, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        });

        if (!userData) return;

        $('#editModal #id').val(userData.id).prop('disabled', true);
        $('#editModal #name').val(userData.name);
        $('#editModal #surname').val(userData.surname);
        $('#editModal #email').val(userData.username);
        $('#editModal #password').val('');

        userData.roleIds.forEach(role => {
            $('#editModal #form-select-roles option[value="' + role + '"]').prop('selected', true);
        });

        $('#editModal').modal('show');
    }

    async function addUser() {
        const data = {
            name: $('#user-add-content #name').val(),
            surname: $('#user-add-content #surname').val(),
            username: $('#user-add-content #email').val(),
            password: $('#user-add-content #password').val(),
            roleIds: $('#user-add-content #form-select-roles').val(),
        };

        const responseData = await apiRequest(HOST + '/api/add-user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!responseData) return;

        addUserToUserList(responseData);

        $('#user-add-content input').val('');
        $('#user-add-content #form-select-roles').val('');
    }

    async function apiRequest(url, options = {}) {
        try {
            const response = await fetch(url, options);

            const isJson = response.headers.get("content-type")?.includes("application/json");
            const data = isJson ? await response.json() : null;

            if (!response.ok) {
                const message = data?.message || 'Произошла ошибка';
                errorMessage(message);
                throw new Error(message);
            }

            return data;

        } catch (error) {
            console.log(error.message);
        }
    }

    function errorMessage(message) {
        $('#toast-error-message').text(message);
        $('#liveToast').toast('show');
    }

    function addUserToUserList(userData) {
        var content = '<tr id="' + userData.id + '">' +
            '<td scope="row" id="user-id">' + userData.id + '</td>' +
            '<td scope="row" id="user-name">' + userData.name + '</td>' +
            '<td scope="row" id="user-surname">' + userData.surname + '</td>' +
            '<td scope="row" id="user-email">' + userData.username + '</td>' +
            '<td scope="row" id="user-roles">' + userData.roles + '</td>' +
            '<td scope="row" id="edit-user"><button type="button" class="btn btn-info">Edit</button></td>' +
            '<td scope="row" id="delete-user"><button type="button" class="btn btn-danger">Delete</button></td>' +
            '</tr>'

        $('#nav-admin #users-list-table tbody').append(content);
    }
});