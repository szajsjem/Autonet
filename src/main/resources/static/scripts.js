    function fetchData(url, callback) {
      fetch(url)
        .then(response => response.json())
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
    }
    function fetchRawData(url, callback) {
          fetch(url)
            .then(response => callback(response))
            .catch(error => console.error('Error:', error));
        }
    function fetchRawPost(url, callback){
        fetch(url, {
            method: 'POST'
        })
        .then(response => callback(response))
        .catch(error => console.error('Error:', error));
    }
    function fetchPost(url, data, callback) {
    if(data===null){
      fetch(url, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
    }
    else{
      fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(response => response.json())
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
    }
    }
    function fetchPut(url,data, callback) {
      fetch(url, {
        method: 'PUT',
        headers: {
            'Content-Type': 'text/html'
        },
        body: data
        })
        .then(response => response.json())
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
    }
    function fetchDelete(url, callback) {
      fetch(url, {
            method: 'DELETE'
        })
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
    }


    function updateNavbar() {
      const navbar = document.getElementById('topNavbar');
      fetchData('/api/user/testlogin', (loginStatus) => {
        if (loginStatus.ok === true) {
          fetchData('/api/user', (userData) => {
            navbar.innerHTML = `
              <a id="logo_href" href="#" onclick="goToMain()"><img src="/logo.png" id="logo_img"></a>
              <span>Welcome, ${userData.data.login}!</span>
              <button onclick="goToProfile()">Profile</button>
              <button onclick="logout()">Logout</button>
            ` + getgenpagebuttons();
          });
        } else {
          navbar.innerHTML = `
              <a id="logo_href" href="#" onclick="goToMain()"><img src="/logo.png" id="logo_img"></a>
              <button onclick="goToLogin()">Login</button>
              <button onclick="goToRegister()">Register</button>
          ` + getgenpagebuttons();
        }
      });
    }
    function logout() {
        fetchDelete('/api/user/logout',(data)=>{
            goToMain();
        });
    }
    function searchWiki(event) {
        event.preventDefault();

        const form = document.getElementById('searchForm');
        var path = form.search.value;

        window.location.href= '/wiki/'+path.split(' ').join('_') + '.html';
    }
    function loginUser(event) {
          event.preventDefault();

          const form = document.getElementById('loginForm');
          const username = form.username.value;
          const password = form.password.value;

          const data = {
            login: username,
            password: password
          };

          fetchPost('/api/user/login',data,result => {
            const messageDiv = document.getElementById('loginMessage');
            if (result.ok === true) {
              messageDiv.innerHTML = 'Login successful.';
              goToMain();
            } else {
              messageDiv.innerHTML = `Login failed: ${result.data.message}`;
            }
          });
        }
    function registerUser(event) {
          event.preventDefault();

          const form = document.getElementById('registrationForm');
          const username = form.username.value;
          const email = form.email.value;
          const password = form.password.value;

          const data = {
            login: username,
            email: email,
            password: password
          };

          fetchPost('/api/user/register',data, result => {
            const messageDiv = document.getElementById('registrationMessage');
            if (result.ok === true) {
              messageDiv.innerHTML = 'Registration successful.';
              goToLogin();
            } else {
              messageDiv.innerHTML = `Registration failed: ${result.data.message}`;
            }
          });
        }

    function updateProfile(event){
        event.preventDefault();
        const form = document.getElementById('registrationForm');
        const pageGenSystemText = form.pageGenSystemText.value;
        const pageGenUserText = form.pageGenUserText.value;
        const selectedModel = form.selectedModel.value;

        const data = {
            login: username,
            email: email,
            password: password
        };

        fetchPut('/api/user', data, (resp)=>{
            if(resp.ok === true){
                messageDiv.innerHTML = 'Update successful.';
            }
            else{
                messageDiv.innerHTML = `Update failed: ${resp.errorMessage}`;
            }
        });
    }
    function updateProfileF() {
        fetchData('/api/user', (userData) => {
            const form = document.getElementById('profileForm');
            if(userData.ok === true){
                form.pageGenSystemText.value = userData.data.generationInstructions.pageGenSystemText;
                form.pageGenUserText.value = userData.data.generationInstructions.pageGenUserText;
                form.selectedModel.value = userData.data.generationInstructions.selectedModel;
            }
            const dataDiv = document.getElementById('login');
            dataDiv.innerHTML = "Login:"+userData.data.login;
            const data2Div = document.getElementById('email');
            data2Div.innerHTML = "Email:"+userData.data.email;
        });
    }

    var editor=null;
    function updateEditor() {
        editor = CKEDITOR.ClassicEditor.create(document.querySelector("#editor"), {
                                        toolbar: {
                                            items: [
                                                'insertImage', 'mediaEmbed', 'link', '|',
                                                'specialCharacters', 'horizontalLine ', 'blockQuote', 'pageBreak', '|',
                                                'findAndReplace', 'selectAll', 'sourceEditing', '|',
                                                'undo', 'redo',
                                                '-',
                                                'heading', '|',
                                                'fontFamily', 'fontSize', '|',
                                                'bold', 'italic', 'underline', '|',
                                                'outdent', 'indent', 'alignment', '|',
                                                'bulletedList', 'numberedList', '|'
                                            ],
                                            shouldNotGroupWhenFull: true
                                        },
                                        language: 'pl',
                                        list: {
                                            properties: {
                                                styles: true,
                                                startIndex: true,
                                                reversed: true
                                            }
                                        },
                                        heading: {
                                            options: [
                                                {model: 'paragraph', title: 'Akapit', class: 'ck-heading_paragraph'},
                                                {model: 'heading1', view: 'h1', title: 'Nagłówek 1', class: 'ck-heading_heading1'},
                                                {model: 'heading2', view: 'h2', title: 'Nagłówek 2', class: 'ck-heading_heading2'},
                                                {model: 'heading3', view: 'h3', title: 'Nagłówek 3', class: 'ck-heading_heading3'},
                                                {model: 'heading4', view: 'h4', title: 'Nagłówek 4', class: 'ck-heading_heading4'},
                                                {model: 'heading5', view: 'h5', title: 'Nagłówek 5', class: 'ck-heading_heading5'},
                                                {model: 'heading6', view: 'h6', title: 'Nagłówek 6', class: 'ck-heading_heading6'}
                                            ]
                                        },
                                        placeholder: 'Opis',
                                        fontFamily: {
                                            options: [
                                                'default',
                                                'Arial, Helvetica, sans-serif',
                                                'Courier New, Courier, monospace',
                                                'Georgia, serif',
                                                'Lucida Sans Unicode, Lucida Grande, sans-serif',
                                                'Tahoma, Geneva, sans-serif',
                                                'Times New Roman, Times, serif',
                                                'Trebuchet MS, Helvetica, sans-serif',
                                                'Verdana, Geneva, sans-serif'
                                            ],
                                            supportAllValues: true
                                        },
                                        fontSize: {
                                            options: [9, 10, 11, 12, 13, 14, 16, 18, 20, 24, 32, 48],
                                            supportAllValues: true
                                        },
                                        htmlSupport: {
                                            allow: [
                                                {
                                                    name: /.*/,
                                                    attributes: true,
                                                    classes: true,
                                                    styles: true
                                                }
                                            ]
                                        },
                                        htmlEmbed: {
                                            showPreviews: true
                                        },
                                        link: {
                                            decorators: {
                                                addTargetToExternalLinks: true,
                                                defaultProtocol: 'https://',
                                                toggleDownloadable: {
                                                    mode: 'manual',
                                                    label: 'Downloadable',
                                                    attributes: {
                                                        download: 'file'
                                                    }
                                                }
                                            }
                                        },
                                        mention: {
                                            feeds: [
                                                {
                                                    marker: '@',
                                                    feed: [
                                                        '@apple', '@bears', '@brownie', '@cake', '@cake', '@candy', '@canes', '@chocolate', '@cookie', '@cotton', '@cream',
                                                        '@cupcake', '@danish', '@donut', '@dragée', '@fruitcake', '@gingerbread', '@gummi', '@ice', '@jelly-o',
                                                        '@liquorice', '@macaroon', '@marzipan', '@oat', '@pie', '@plum', '@pudding', '@sesame', '@snaps', '@soufflé',
                                                        '@sugar', '@sweet', '@topping', '@wafer'
                                                    ],
                                                    minimumCharacters: 1
                                                }
                                            ]
                                        },
                                        removePlugins: [
                                            'CKBox',
                                            'CKFinder',
                                            'EasyImage',
                                            'RealTimeCollaborativeComments',
                                            'RealTimeCollaborativeTrackChanges',
                                            'RealTimeCollaborativeRevisionHistory',
                                            'PresenceList',
                                            'Comments',
                                            'TrackChanges',
                                            'TrackChangesData',
                                            'RevisionHistory',
                                            'Pagination',
                                            'WProofreader',
                                            'MathType',
                                            'SlashCommand',
                                            'Template',
                                            'DocumentOutline',
                                            'FormatPainter',
                                            'TableOfContents'
                                        ]
                                    });
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const data = {
            page: urlParams.get('page')
        };
        fetchPost('/api/isGenerated',data,result => {
            if (result.ok === true) {
                fetchRawData(data.page,resp =>{
                    resp.text().then(text=>{
                        editor.then( editorInstance => {
                            editorInstance.setData(text);
                        });
                    })
                });
            }
        });
    }

    function updatePage(){
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const path = '/edit' + urlParams.get('page').slice(5);
        editor.then( editorInstance => {
            const data = editorInstance.getData();
            fetchPut(path,data,resp =>{
                const dataDiv = document.getElementById('saveMessage');
                if(resp.ok===true){
                    dataDiv.innerHTML = 'Pomyślnie zapisano';
                }
                else{
                    dataDiv.innerHTML = 'Wystąpił problem z zapisaniem';
                }
            })
        });
    }


    function getgenpagebuttons(){
        if(window.location.href.includes('\/editor.html'))
            return `
                    <button onclick="goToEView()">View</button>
                    `;
        if(window.location.href.includes('\/log'))
            return `
                    <button onclick="goToView()">View</button>
                    `;
        if(window.location.href.includes('\/wiki'))
            return `
                    <button onclick="goToEdit()">Edit</button>
                    <button onclick="goToLogs()">Logs</button>
                    `;
        return ``;
    }




    function goToGenerated(){
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const data = {
            page: urlParams.get('page')
        };
        fetchPost('/api/isGenerated',data,result => {
            const messageDiv = document.getElementById('loadingMessage');
            if (result.ok === true) {
                window.location.href = data.page;
            } else {
                if ( result.message !== null ){
                    messageDiv.innerHTML = result.message;
                }
                else{
                    setTimeout(goToGenerated,5E3);
                }
            }
        });
    }

    function goToLogs(){
        window.location.href= '/log'+window.location.pathname;
    }
    function goToEdit(){
        window.location.href= '/editor.html?page='+window.location.pathname;
    }
    function goToEView(){
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const path = urlParams.get('page');
        window.location.href = path;
    }
    function goToView() {
        window.location.href = window.location.pathname.slice(4);
    }

    function goToMain(){
      window.location.href = '/';
    }
    function goToLogin() {
      window.location.href = '/login.html';
    }
    function goToRegister() {
      window.location.href = '/register.html';
    }
    function goToProfile() {
      window.location.href = '/profile.html';
    }

