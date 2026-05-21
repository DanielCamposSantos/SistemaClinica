package io.github.danielcampossantos.domain;

public class Paciente {
    private Long id;
    private String nome;
    private String cpf;
    private String rg;
    private String telefone;
    private String email;

    public Paciente(Long id, String nome, String cpf, String rg, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.telefone = telefone;
        this.email = email;
    }

    public static PacienteBuilder builder() {
        return new PacienteBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCpf() {
        return this.cpf;
    }

    public String getRg() {
        return this.rg;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public String getEmail() {
        return this.email;
    }

    public String toString() {
        return "Paciente(id=" + this.getId() + ", nome=" + this.getNome() + ", cpf=" + this.getCpf() + ", rg=" + this.getRg() + ", telefone=" + this.getTelefone() + ", email=" + this.getEmail() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Paciente)) return false;
        final Paciente other = (Paciente) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Paciente;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    public static class PacienteBuilder {
        private Long id;
        private String nome;
        private String cpf;
        private String rg;
        private String telefone;
        private String email;

        PacienteBuilder() {
        }

        public PacienteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PacienteBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public PacienteBuilder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public PacienteBuilder rg(String rg) {
            this.rg = rg;
            return this;
        }

        public PacienteBuilder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public PacienteBuilder email(String email) {
            this.email = email;
            return this;
        }

        public Paciente build() {
            return new Paciente(this.id, this.nome, this.cpf, this.rg, this.telefone, this.email);
        }

        public String toString() {
            return "Paciente.PacienteBuilder(id=" + this.id + ", nome=" + this.nome + ", cpf=" + this.cpf + ", rg=" + this.rg + ", telefone=" + this.telefone + ", email=" + this.email + ")";
        }
    }
}
