function function1(){

    var password = document.getElementById('password');
    var icon = document.getElementById('icon1');


    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    const source = icon.getAttribute('src') === 'img/view.png' ? 'img/hide.png' : 'img/view.png';
    password.setAttribute('type', type);
    icon.setAttribute('src', source);

    
}