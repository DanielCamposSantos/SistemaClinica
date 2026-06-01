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
    if (!valor) return 'R$ 0,00';
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

function formatarTelefone(paciente) {
    if (!paciente.ddd || !paciente.telefone) {
        return 'Não informado';
    }
    return `(${paciente.ddd}) ${paciente.telefone}`;
}

function preencherSelectAno() {
    const selectAno = document.getElementById('filtroAno');
    if (!selectAno) return;

    selectAno.innerHTML = '';
    const anoAtual = new Date().getFullYear();
    for (let ano = anoAtual; ano >= anoAtual - 5; ano--) {
        const option = document.createElement('option');
        option.value = ano;
        option.textContent = ano;
        selectAno.appendChild(option);
    }
    selectAno.value = anoAtual;
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
            const valorPlano = paciente.planoValor ? formatarSalario(paciente.planoValor) : 'R$ 0,00';
            const endereco = formatarEndereco(paciente);
            const telefone = formatarTelefone(paciente);

            tabela.innerHTML += `
                <tr>
                    <td>${paciente.nome} ${paciente.sobrenome}</td>
                    <td>${paciente.cpf}</td>
                    <td>${telefone}</td>
                    <td>${paciente.planoDescricao || 'Sem plano'}</td>
                    <td>${valorPlano}</td>
                    <td>${dataNasc}</td>
                    <td>${endereco}</td>
                    <td>
                        <button onclick="buscarPacientePorId(${paciente.id})" class="btn-acao">Ver</button>
                        <button onclick="buscarProntuario(${paciente.id})" class="btn-acao">Prontuário</button>
                        <button onclick="buscarResultados(${paciente.id})" class="btn-acao">Resultados</button>
                        <button onclick="buscarReceitas(${paciente.id})" class="btn-acao">Receitas</button>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
    }
}

async function buscarPacientePorId(id) {
    try {
        const resposta = await fetch(`http://localhost:8080/pacientes/${id}`);
        const paciente = await resposta.json();

        const container = document.getElementById('detalhePacienteConteudo');
        const dataNasc = formatarData(
            paciente.dataNascimento.day,
            paciente.dataNascimento.month,
            paciente.dataNascimento.year
        );
        const valorPlano = paciente.planoValor ? formatarSalario(paciente.planoValor) : 'R$ 0,00';
        const endereco = formatarEndereco(paciente);
        const telefone = formatarTelefone(paciente);

        container.innerHTML = `
            <div class="detalhe-card">
                <p><strong>Nome:</strong> ${paciente.nome} ${paciente.sobrenome}</p>
                <p><strong>CPF:</strong> ${paciente.cpf}</p>
                <p><strong>Telefone:</strong> ${telefone}</p>
                <p><strong>Data de Nascimento:</strong> ${dataNasc}</p>
                <p><strong>Plano:</strong> ${paciente.planoDescricao || 'Sem plano'}</p>
                <p><strong>Valor do Plano:</strong> ${valorPlano}</p>
                <p><strong>Endereço:</strong> ${endereco}</p>
            </div>
        `;

        mostrarTela('detalhePaciente');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar paciente');
    }
}

async function buscarProntuario(pacienteId) {
    try {
        const resposta = await fetch(`http://localhost:8080/pacientes/prontuarios/${pacienteId}`);
        const prontuario = await resposta.json();

        const container = document.getElementById('prontuarioConteudo');

        let examesHTML = '';
        if (prontuario.examesComResultados && Object.keys(prontuario.examesComResultados).length > 0) {
            examesHTML = '<ul>';
            for (const [exame, resultados] of Object.entries(prontuario.examesComResultados)) {
                examesHTML += `<li><strong>${exame}:</strong> ${resultados.join(', ')}</li>`;
            }
            examesHTML += '</ul>';
        } else {
            examesHTML = '<p>Nenhum exame registrado</p>';
        }

        let diagnosticosHTML = prontuario.diagnosticos && prontuario.diagnosticos.length > 0
            ? '<ul>' + prontuario.diagnosticos.map(d => `<li>${d}</li>`).join('') + '</ul>'
            : '<p>Nenhum diagnóstico registrado</p>';

        let tratamentosHTML = prontuario.tratamentos && prontuario.tratamentos.length > 0
            ? '<ul>' + prontuario.tratamentos.map(t => `<li>${t}</li>`).join('') + '</ul>'
            : '<p>Nenhum tratamento registrado</p>';

        container.innerHTML = `
            <div class="detalhe-card">
                <p><strong>Paciente:</strong> ${prontuario.nomePaciente}</p>
                <p><strong>Médico:</strong> ${prontuario.nomeMedico}</p>
                <h3>Exames e Resultados</h3>
                ${examesHTML}
                <h3>Diagnósticos</h3>
                ${diagnosticosHTML}
                <h3>Tratamentos</h3>
                ${tratamentosHTML}
            </div>
        `;

        mostrarTela('prontuario');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar prontuário');
    }
}

async function buscarResultados(pacienteId) {
    try {
        const resposta = await fetch(`http://localhost:8080/pacientes/${pacienteId}/resultados`);
        const resultados = await resposta.json();

        const tabela = document.getElementById('listaResultados');
        tabela.innerHTML = '';

        if (resultados.length === 0) {
            tabela.innerHTML = '<tr><td colspan="7">Nenhum resultado encontrado</td></tr>';
        } else {
            resultados.forEach(resultado => {
                const dataConsulta = resultado.dataConsulta
                    ? formatarData(
                        resultado.dataConsulta.day,
                        resultado.dataConsulta.month,
                        resultado.dataConsulta.year
                    )
                    : '-';

                const horario = resultado.horario
                    ? formatarHorario(resultado.horario.hour, resultado.horario.minute)
                    : '-';

                tabela.innerHTML += `
                    <tr>
                        <td>${resultado.idConsulta}</td>
                        <td>${resultado.medicoNome}</td>
                        <td>${resultado.especialidade}</td>
                        <td>${dataConsulta}</td>
                        <td>${horario}</td>
                        <td>${resultado.diagnostico || '-'}</td>
                        <td>${resultado.tratamento || '-'}</td>
                    </tr>
                `;
            });
        }

        mostrarTela('resultados');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar resultados');
    }
}

async function buscarReceitas(pacienteId) {
    try {
        const resposta = await fetch(`http://localhost:8080/pacientes/${pacienteId}/receitas`);

        if (!resposta.ok) {
            const erro = await resposta.json();
            alert(erro.erro);
            return;
        }

        const receitas = await resposta.json();

        const container = document.getElementById('listaReceitas');
        container.innerHTML = '';

        if (receitas.length === 0) {
            container.innerHTML = '<p>Nenhuma receita encontrada</p>';
        } else {
            receitas.forEach(receita => {
                const remediosHTML = receita.remedios && receita.remedios.length > 0
                    ? receita.remedios.map(r => `<li>${r.nomeRemedio} - ${r.posologia}</li>`).join('')
                    : '<li>Nenhum remédio prescrito</li>';

                const data = receita.dataHorario.date;
                const hora = receita.dataHorario.time;
                const dataFormatada = formatarData(data.day, data.month, data.year);
                const horaFormatada = formatarHorario(hora.hour, hora.minute);

                container.innerHTML += `
                    <div class="detalhe-card">
                        <p><strong>Receita #${receita.idReceita}</strong></p>
                        <p><strong>Título:</strong> ${receita.tituloReceita}</p>
                        <p><strong>Médico:</strong> ${receita.medicoNome} (CRM: ${receita.crmMedico})</p>
                        <p><strong>Data/Hora:</strong> ${dataFormatada} ${horaFormatada}</p>
                        <h4>Remédios:</h4>
                        <ul>${remediosHTML}</ul>
                    </div>
                `;
            });
        }

        mostrarTela('receitas');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar receitas');
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
                    <td>
                        <button onclick="buscarMedicoPorId(${medico.id})" class="btn-acao">Ver</button>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
    }
}

async function buscarMedicoPorId(id) {
    try {
        const resposta = await fetch(`http://localhost:8080/medicos/${id}`);
        const medico = await resposta.json();

        const container = document.getElementById('detalheMedicoConteudo');
        const salario = formatarSalario(medico.salario);

        container.innerHTML = `
            <div class="detalhe-card">
                <p><strong>Nome:</strong> ${medico.nome} ${medico.sobrenome}</p>
                <p><strong>CRM:</strong> ${medico.crm}</p>
                <p><strong>Especialidade:</strong> ${medico.especialidade}</p>
                <p><strong>Salário:</strong> ${salario}</p>
            </div>
        `;

        mostrarTela('detalheMedico');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar médico');
    }
}

async function carregarAgendamentos() {
    try {
        preencherSelectAno();

        const resposta = await fetch('http://localhost:8080/agendamentos');
        const agendamentos = await resposta.json();
        const tabela = document.getElementById('listaAgendamentos');
        tabela.innerHTML = '';
        document.getElementById('qtdAgendamentos').innerText = agendamentos.length;

        const mesAtual = new Date().getMonth() + 1;
        document.getElementById('filtroMes').value = mesAtual;

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
                    <td>
                        <button onclick="filtrarAgendamentosPorPaciente(${agendamento.id})" class="btn-acao">Ver Paciente</button>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
    }
}

async function filtrarAgendamentosPorMes() {
    try {
        const mes = document.getElementById('filtroMes').value;
        const ano = document.getElementById('filtroAno').value;

        const resposta = await fetch(`http://localhost:8080/agendamentos?mes=${mes}&ano=${ano}`);
        const agendamentos = await resposta.json();

        const tabela = document.getElementById('listaAgendamentos');
        tabela.innerHTML = '';

        if (agendamentos.length === 0) {
            tabela.innerHTML = '<tr><td colspan="9">Nenhum agendamento encontrado para este período</td></tr>';
            document.getElementById('qtdAgendamentos').innerText = '0';
            return;
        }

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
                    <td>
                        <button onclick="filtrarAgendamentosPorPaciente(${agendamento.id})" class="btn-acao">Ver Paciente</button>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        console.error(e);
        alert('Erro ao filtrar agendamentos');
    }
}

async function filtrarAgendamentosPorPaciente(agendamentoId) {
    try {
        const resposta = await fetch(`http://localhost:8080/agendamentos?paciente=${agendamentoId}`);
        const agendamentos = await resposta.json();

        const tabela = document.getElementById('listaAgendamentosPaciente');
        tabela.innerHTML = '';

        if (agendamentos.length === 0) {
            tabela.innerHTML = '<tr><td colspan="8">Nenhum agendamento encontrado para este paciente</td></tr>';
        } else {
            agendamentos.forEach(ag => {
                const dataAgend = formatarData(
                    ag.dataAgendamento.day,
                    ag.dataAgendamento.month,
                    ag.dataAgendamento.year
                );
                const dataCons = formatarData(
                    ag.dataConsulta.day,
                    ag.dataConsulta.month,
                    ag.dataConsulta.year
                );
                const horario = formatarHorario(
                    ag.horario.hour,
                    ag.horario.minute
                );
                const valor = formatarSalario(ag.valor);

                tabela.innerHTML += `
                    <tr>
                        <td>${ag.pacienteNome}</td>
                        <td>${ag.medicoNome}</td>
                        <td>${ag.especialidade}</td>
                        <td>${ag.atendenteNome}</td>
                        <td>${valor}</td>
                        <td>${dataAgend}</td>
                        <td>${dataCons}</td>
                        <td>${horario}</td>
                    </tr>
                `;
            });
        }

        mostrarTela('agendamentosPaciente');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar agendamentos do paciente');
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
                    <td>
                        <button onclick="buscarAtendentePorId(${atendente.id})" class="btn-acao">Ver</button>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        console.error('Erro ao carregar atendentes:', e);
        alert('Não foi possível carregar os dados dos atendentes.');
    }
}

async function buscarAtendentePorId(id) {
    try {
        const resposta = await fetch(`http://localhost:8080/atendentes/${id}`);
        const atendente = await resposta.json();

        const container = document.getElementById('detalheAtendenteConteudo');
        const dataNasc = formatarData(
            atendente.dataNascimento.day,
            atendente.dataNascimento.month,
            atendente.dataNascimento.year
        );
        const salario = formatarSalario(atendente.salario);

        container.innerHTML = `
            <div class="detalhe-card">
                <p><strong>Nome:</strong> ${atendente.nome} ${atendente.sobrenome}</p>
                <p><strong>Email:</strong> ${atendente.email}</p>
                <p><strong>Salário:</strong> ${salario}</p>
                <p><strong>Data de Nascimento:</strong> ${dataNasc}</p>
            </div>
        `;

        mostrarTela('detalheAtendente');
    } catch (e) {
        console.error(e);
        alert('Erro ao buscar atendente');
    }
}

window.onload = () => {
    carregarPacientes();
    carregarMedicos();
    carregarAgendamentos();
    carregarAtendentes();
};