function mostrarTela(id) {
    const telas = document.querySelectorAll('.tela');
    telas.forEach(tela => {
        tela.classList.remove('ativa');
    });
    document.getElementById(id).classList.add('ativa');
}

function formatarData(day, month, year) {
    const dia = String(day).padStart(2, '0');
    const mes = String(month).padStart(2, '0');
    return `${dia}/${mes}/${year}`;
}

function formatarHorario(hour, minute) {
    const hora = String(hour).padStart(2, '0');
    const min = String(minute).padStart(2, '0');
    return `${hora}:${min}`;
}

function formatarSalario(valor) {
    return valor.toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}

function formatarEndereco(paciente) {
    let endereco = `${paciente.rua}, ${paciente.numero} - ${paciente.bairro}, ${paciente.cidade}`;
    if (paciente.complemento) {
        endereco += ` (${paciente.complemento})`;
    }
    return endereco;
}

async function carregarPacientes() {
    try {
        const resposta = await fetch('http://localhost:8080/pacientes');
        const pacientes = await resposta.json();
        const tabela = document.getElementById('listaPacientes');
        tabela.innerHTML = '';
        document.getElementById('qtdPacientes').innerText = pacientes.length;

        pacientes.forEach(paciente => {
            const dataNasc = formatarData(
                paciente.dataNascimento.day,
                paciente.dataNascimento.month,
                paciente.dataNascimento.year
            );
            const valorPlano = formatarSalario(paciente.planoValor);
            const endereco = formatarEndereco(paciente);

            tabela.innerHTML += `
                <tr>
                    <td>${paciente.nome} ${paciente.sobrenome}</td>
                    <td>${paciente.cpf}</td>
                    <td>${paciente.planoDescricao}</td>
                    <td>${valorPlano}</td>
                    <td>${dataNasc}</td>
                    <td>${endereco}</td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
    }
}

async function carregarMedicos() {
    try {
        const resposta = await fetch('http://localhost:8080/medicos');
        const medicos = await resposta.json();
        const tabela = document.getElementById('listaMedicos');
        tabela.innerHTML = '';
        document.getElementById('qtdMedicos').innerText = medicos.length;

        medicos.forEach(medico => {
            const salario = formatarSalario(medico.salario);

            tabela.innerHTML += `
                <tr>
                    <td>${medico.nome} ${medico.sobrenome}</td>
                    <td>${medico.crm}</td>
                    <td>${medico.especialidade}</td>
                    <td>${salario}</td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
    }
}

async function carregarAgendamentos() {
    try {
        const resposta = await fetch('http://localhost:8080/agendamentos');
        const agendamentos = await resposta.json();
        const tabela = document.getElementById('listaAgendamentos');
        tabela.innerHTML = '';
        document.getElementById('qtdAgendamentos').innerText = agendamentos.length;

        agendamentos.forEach(agendamento => {
            const dataAgend = formatarData(
                agendamento.dataAgendamento.day,
                agendamento.dataAgendamento.month,
                agendamento.dataAgendamento.year
            );
            const dataCons = formatarData(
                agendamento.dataConsulta.day,
                agendamento.dataConsulta.month,
                agendamento.dataConsulta.year
            );
            const horario = formatarHorario(
                agendamento.horario.hour,
                agendamento.horario.minute
            );
            const valor = formatarSalario(agendamento.valor);

            tabela.innerHTML += `
                <tr>
                    <td>${agendamento.pacienteNome}</td>
                    <td>${agendamento.medicoNome}</td>
                    <td>${agendamento.especialidade}</td>
                    <td>${agendamento.atendenteNome}</td>
                    <td>${valor}</td>
                    <td>${dataAgend}</td>
                    <td>${dataCons}</td>
                    <td>${horario}</td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
    }
}

async function carregarAtendentes() {
    try {
        const resposta = await fetch('http://localhost:8080/atendentes');
        const atendentes = await resposta.json();
        const tabela = document.getElementById('listaAtendentes');
        tabela.innerHTML = '';

        atendentes.forEach(atendente => {
            const dataFormatada = formatarData(
                atendente.dataNascimento.day,
                atendente.dataNascimento.month,
                atendente.dataNascimento.year
            );
            const salarioFormatado = formatarSalario(atendente.salario);

            tabela.innerHTML += `
                <tr>
                    <td>${atendente.nome} ${atendente.sobrenome}</td>
                    <td>${atendente.email}</td>
                    <td>${salarioFormatado}</td>
                    <td>${dataFormatada}</td>
                </tr>
            `;
        });
    } catch (e) {
        console.error('Erro ao carregar atendentes:', e);
        alert('Não foi possível carregar os dados dos atendentes.');
    }
}

window.onload = () => {
    carregarPacientes();
    carregarMedicos();
    carregarAgendamentos();
    carregarAtendentes();
};