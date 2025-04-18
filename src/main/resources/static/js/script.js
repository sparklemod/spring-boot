$(document).ready(function(){

    const HOST = 'http://localhost:8080';

    function errorMessage(message) {
        $('#liveToast').toast('show');
        $('#toast-error-message').text(message);
    }

    function addUserToUserList(userData) {
        var content = '<tr id="' + userData.id + '">' +
            '<td scope="row" id="user-id">' + userData.id + '</td>' +
            '<td scope="row" id="user-name">' + userData.name + '</td>' +
            '<td scope="row" id="user-surname">' + userData.surname + '</td>' +
            '<td scope="row" id="age">' + userData.age + '</td>' +
            '<td scope="row" id="user-email">' + userData.email + '</td>' +
            '<td scope="row" id="user-roles">[' + userData.roles + ']</td>' + //todo строка или нет????
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
                url: HOST + '/users',
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
            $('#header-username').text(data.email);
            $('#header-roles').text(data.roles);

            if (data.roles.search(/\bADMIN\b/) < 0) {
                $('#nav-admin').remove();
                $('#nav-user').addClass('active-section');
                $('#admin-btn').remove();
                $('#user-btn').addClass('active');
            }

            $('#user-info').html(data.email + ' with roles ' + data.roles);
        },
        error: function(xhr) {
            errorMessage(xhr.responseText)
        }
    });

    // About User
    $.ajax({
        type: "GET",
        url: HOST + '/user',
        dataType: 'json',
        contentType: 'application/json',
        success: function(data) {
            $('#nav-user #user-id').text(data.id);
            $('#nav-user #user-name').text(data.name);
            $('#nav-user #user-surname').text(data.surname);
            $('#nav-user #age').text(data.age);
            $('#nav-user #user-email').text(data.email);
            $('#nav-user #user-roles').text('[' + data.roles + ']');
        },
        error: function(xhr) {
            errorMessage(xhr.responseText)
        }
    });

    //All Users
    $.ajax({
        type: "GET",
        url: HOST + '/users',
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

        var data = {
            firstName: $('#user-add-content #name').val(),
            lastName: $('#user-add-content #surname').val(),
            email: $('#user-add-content #email').val(),
            age: $('#user-add-content #age').val(),
            password: $('#user-add-content #password').val(),
            roles: $('#user-add-content #form-select-roles').val() //todo а нужна ошибка если пусто???
        }

        $.ajax({
            type: "POST",
            url: HOST + '/user',
            data: data,
            dataType: 'json',
            contentType: 'application/json',
            success: function(responseData) {
                addUserToUserList(responseData);

                $('#user-add-content #name').val('');
                $('#user-add-content #surname').val('');
                $('#user-add-content #age').val('');
                $('#user-add-content #email').val('');
                $('#user-add-content #password').val('');
                $('#user-add-content #form-select-roles').val('');
            },
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
    })

    function getOptionRoles(element) {
        $.ajax({
            type: "GET",
            url: HOST + '/roles',
            dataType: 'json',
            contentType: 'application/json',
            success: function(roles) {
                roles.forEach(role => {
                    var option = '<option value="' + role.id + '">' + role.role + '</option>';
                    element.append(option);
                });
            },
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
    }

    // Edit User (Open Modal)
    $('#all-users-list-content').delegate('#edit-user button', 'click', function(e) {
        var userElement = $(this).closest('tr');
        var userId = userElement.attr('id');

        $('#editModal').modal('show');

        getOptionRoles($('#editModal #form-select-roles'));

        $.ajax({
            type: "GET",
            url: HOST + '/user/' + userId,
            dataType: 'json',
            contentType: 'application/json',
            success: function(userData) {
                $('#editModal #id').val(userData.id);
                $('#editModal #name').val(userData.name);
                $('#editModal #surname').val(userData.surname);
                $('#editModal #age').val(userData.age);
                $('#editModal #email').val(userData.email);

                userData.roles.forEach(role => {
                    $('#editModal #form-select-roles option[value="' + role + '"]').attr('selected', 'selected');
                });
            },
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
    });

    // Edit User (request)
    $('#editModal #edit-user').click(function(e) {
        e.preventDefault();

        var userId = $('#editModal #id').val();

        var data = {
            firstName: $('#editModal #name').val(),
            lastName: $('#editModal #surname').val(),
            email: $('#editModal #email').val(),
            password: $('#editModal #password').val(),
            age: $('#editModal #age').val(),
            roles: $('#editModal #form-select-roles').val()
        }

        $.ajax({
            type: "PUT",
            url: HOST + '/user/' + userId,
            dataType: 'json',
            data: data,
            contentType: 'application/json',
            success: function(userData) {
                $('#editModal #name').val('');
                $('#editModal #surname').val('');
                $('#editModal #email').val('');
                $('#editModal #age').val('');
                $('#editModal #password').val('');
                $('#editModal #form-select-roles').val('');

                var parent = $('#nav-admin #users-list-table tbody #' + userId);

                parent.find('#user-name').text(userData.firstName);
                parent.find('#user-surname').text(userData.lastName);
                parent.find('#user-email').text(userData.email);
                parent.find('#user-roles').text(userData.roles);

                $('#editModal').modal('hide');
            },
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
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

    // Logout
    $('#logout-btn').click(function() {
        $.ajax({
            type: "POST",
            url: HOST + '/logout',
            dataType: 'json',
            data: data,
            contentType: 'application/json',
            error: function(xhr) {
                errorMessage(xhr.responseText)
            }
        });
    });
});