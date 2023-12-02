    function fetchData(url, callback) {
      fetch(url)
        .then(response => response.json())
        .then(data => callback(data))
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
        const path = form.search.value;

        window.location.href= '/wiki/'+path;
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






    function requestPage() {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);

        const data = {
            page: urlParams.get('page')
        };

        fetchPost('/api/generatePage',data,result => {
            const messageDiv = document.getElementById('loadingMessage');
            if (result.ok === true) {
                messageDiv.innerHTML = 'Tworzenie strony';
            } else {
                messageDiv.innerHTML = result.message;
            }
        });
    }
    function updatePage(){

    }
    function getgenpagebuttons(){
        if(window.location.href.includes('\/wiki'))
            return `
                    <button onclick="goToEdit()">Edit</button>
                    <button onclick="goToLogs()">Logs</button>
                    `;
        if(window.location.href.includes('\/log'))
            return `
                    <button onclick="goToView()">View</button>
                    `;
        if(window.location.href.includes('\/editor.html'))
            return `
                    <button onclick="goToEView()">View</button>
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
                let ns = window.location.href;
                ns.replace("/topic.html?search=","/wiki/");
                window.location.href = ns;
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