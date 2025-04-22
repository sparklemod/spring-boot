$(document).ready(function(){

    const HOST = 'http://localhost:8080';

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

    // Switch admin panel Tabs
    $('#userTabs button').click(function() {
        $('#nav-admin .active-tab').removeClass('active-tab');

        var target = $(this).data('target');

        $('#' + target).addClass('active-tab'); //todo посмотреть как без решётки

        if (target == 'user-add-content') {
            getOptionRoles($('#user-add-content #form-select-roles'));
        } else if (target == 'all-users-list-content') {
            $.ajax({
                type: "GET",
                url: HOST + '/user/list',
                dataType: 'json',
                contentType: 'application/json',
                success: function() {

                },
                error: function(xhr) {
                    errorMessage(xhr.responseText)
                }
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

    // Header user info
    $.ajax({
        type: "GET",
        url: HOST + '/user/info',
        dataType: 'json',
        contentType: 'application/json',
        success: function(data) {
            $('#header-username').text(data.username);
            $('#header-roles').text(data.roles);

            if (data.roles.search(/\bADMIN\b/) < 0) {
                $('#nav-admin').remove();
                $('#nav-user').addClass('active-section');
                $('#admin-btn').remove();
                $('#user-btn').addClass('active');
            }

            $('#user-info').html(data.username + ' with roles ' + data.roles);
        },
        error: function(xhr) {
            errorMessage(xhr.responseText)
        }
    });

    // About User
    $.ajax({
        type: "GET",
        url: HOST + '/user/info',
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

    //All Users
    $.ajax({
        type: "GET",
        url: HOST + '/user/list',
        dataType: 'json',
        contentType: 'application/json',
        success: function(users) {
            users.forEach(userData => {
                addUserToUserList(userData);
            });
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
            url: HOST + '/roles',
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
    $('#all-users-list-content').delegate('#edit-user button', 'click', async function(e) {
        const userElement = $(this).closest('tr');
        const userId = userElement.attr('id');

        $('#editModal').modal('show');

        getOptionRoles($('#editModal #form-select-roles'));

        try {
            const response = await fetch(HOST + '/user/' + userId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            const userData = await response.json();

            $('#editModal #id').val(userData.id);
            $('#editModal #name').val(userData.name);
            $('#editModal #surname').val(userData.surname);
            $('#editModal #email').val(userData.username);

            userData.roleIds.forEach(role => {
                $('#editModal #form-select-roles option[value="' + role + '"]').prop('selected', true);
            });

        } catch (error) {
            errorMessage(error.message);
        }
    });

// Edit User (request)
    $('#editModal #edit-user').click(async function(e) {
        e.preventDefault();

        const userId = $('#editModal #id').val();

        const data = {
            name: $('#editModal #name').val(),
            surname: $('#editModal #surname').val(),
            username: $('#editModal #email').val(),
            password: $('#editModal #password').val(),
            roleIds: $('#editModal #form-select-roles').val()
        };

        try {
            const response = await fetch(HOST + '/user/' + userId, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const errorData = await response.json();
                errorMessage(errorData.message);
                return;
            }

            const userData = await response.json();

            $('#editModal').modal('hide');

            const parent = $('#nav-admin #users-list-table tbody #' + userId);

            parent.find('#user-name').text(userData.name);
            parent.find('#user-surname').text(userData.surname);
            parent.find('#user-email').text(userData.username);
            parent.find('#user-roles').text(userData.roleIds);

        } catch (error) {
            console.log(error.message);
        }
    });

    // Delete User (Open modal)
    $('#all-users-list-content').delegate('#delete-user button', 'click', function() {
        var userElement = $(this).closest('tr');

        $('#deleteModal').modal('show');

        getOptionRoles($('#deleteModal #form-select-roles'));
    });

    // Delete User (request)
    $('#deleteModal #delete-user').click(function(e) {
        e.preventDefault();
        var userId = $('#deleteModal #id').val();

        console.log(userId);

        $.ajax({
            type: "DELETE",
            url: HOST + '/user/' + userId,
            dataType: 'json',
            data: data,
            contentType: 'application/json',
            success: function(userData) {
                $('#nav-admin #users-list-table tbody #' + userId).remove();

                $('#deleteModal').modal('hide');
            },
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
    });


    async function addUser() {
        const data = {
            name: $('#user-add-content #name').val(),
            surname: $('#user-add-content #surname').val(),
            username: $('#user-add-content #email').val(),
            password: $('#user-add-content #password').val(),
            roleIds: $('#user-add-content #form-select-roles').val(),
        };

        try {
            const responseData = await apiRequest(HOST + '/user', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!responseData) return;

            addUserToUserList(responseData);

            $('#user-add-content input').val('');
            $('#user-add-content #form-select-roles').val('');

        } catch (error) {
        }
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
            throw error;
        }
    }
});