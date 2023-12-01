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
              <span>Welcome, ${userData.data.login}!</span>
              <button onclick="goToProfile()">Profile</button>
              <button onclick="logout()">Logout</button>
            `;
          });
        } else {
          navbar.innerHTML = `
            <button onclick="goToLogin()">Login</button>
            <button onclick="goToRegister()">Register</button>
          `;
        }
      });
    }
    function logout() {
        fetchDelete('/api/user/logout',(data)=>{
            window.location.href = '/';
        });
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
              window.location.href = '/';
            } else {
              messageDiv.innerHTML = `Login failed: ${result.data.message}`;
            }
          });
        }






    function requestPage(){

    }
    function goToGenerated(){

    }
    function goToLogin() {
      window.location.href = '/login';
    }
    function goToRegister() {
      window.location.href = '/register';
    }
    function goToProfile() {
      window.location.href = '/profile';
    }