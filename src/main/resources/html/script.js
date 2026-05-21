const lista = document.getElementById('lista');

async function carregarLista(){
    try {
        const resposta = await fetch('http://localhost:8080/pacientes');
        const pacientes = await resposta.json()

        lista.innerHTML = '';

        pacientes.forEach((elemento) => {
            const li = document.createElement('li');
            li.textContent = elemento.nome;
            lista.appendChild(li);
        })
    }catch (e) {
        console.error(e);
    }
}

addEventListener("DOMContentLoaded", () => {
    carregarLista()
})