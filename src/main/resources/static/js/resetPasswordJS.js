

function function1(){

    var password = document.getElementById('password2');
    var icon = document.getElementById('icon1');


    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    const source = icon.getAttribute('src') === 'img/view.png' ? 'img/hide.png' : 'img/view.png';
    password.setAttribute('type', type);
    icon.setAttribute('src', source);

    
}

function function2(){

    var password2 = document.getElementById('password1');
    var icon2 = document.getElementById('icon2');


    const type2 = password2.getAttribute('type') === 'password' ? 'text' : 'password';
    const source2 = icon2.getAttribute('src') === 'img/view.png' ? 'img/hide.png' : 'img/view.png';
    password2.setAttribute('type', type2);
    icon2.setAttribute('src', source2);

    
}