# :mobile_phone: ATEC Mobile - Gestão Escolar

> Aplicação Android nativa para gestão de horários, módulos e cursos de Formandos e Formadores.

A **ATEC Mobile** é a extensão móvel do Portal Web da ATEC. Desenvolvida em **Kotlin**, esta aplicação permite que alunos e professores acedam às suas informações académicas em tempo real, com uma interface intuitiva e segura.

---

## :rocket: Funcionalidades Principais

### :closed_lock_with_key: Autenticação e Segurança
* **Login Seguro:** Autenticação via API RESTful com encriptação de dados.
* **2FA (Autenticação de Dois Fatores):** Sistema de segurança extra onde o utilizador recebe um código OTP via Email antes de receber o Token.
* **Gestão de Sessão:** Uso de **JWT (JSON Web Token)** guardado de forma segura em `SharedPreferences` para auto-login.

### :bust_in_silhouette: Dashboard Dinâmica (Role-Based)
A interface adapta-se automaticamente ao tipo de utilizador:
* **Para Formandos:** Visualização do curso inscrito (ex: TGPSI) e lista de unidades curriculares.
* **Para Formadores:** Visualização dos módulos atribuídos para lecionar.
* **Banner Personalizado:** Interface visual apelativa com branding da instituição.

### :date: Agenda e Horários
* **Calendário Interativo:** Navegação por datas para consultar aulas passadas ou futuras.
* **Detalhes da Aula:** Informação sobre horário (Início/Fim), Sala e Módulo.
* **Filtros Inteligentes:** O backend garante que cada utilizador vê apenas o seu horário específico.

---

## :tools: Stack Tecnológica

* **Linguagem:** [Kotlin](https://kotlinlang.org/)
* **Ambiente:** Android Studio Ladybug/Koala
* **Comunicação API:** [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
* **Parsing JSON:** [GSON](https://github.com/google/gson)
* **Assincronismo:** [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)
* **Layouts:** XML (ConstraintLayout, LinearLayout, NestedScrollView)
* **Componentes UI:** Material Design, CalendarView, CardView.

---

## :gear: Configuração e Instalação

### Pré-requisitos
1.  Ter o **Backend Node.js** a correr localmente (Porta 3000).
2.  Android Studio instalado.

### Passo a Passo

1.  **Clonar o Repositório:**
    ```bash
    git clone [https://github.com/teu-utilizador/atec-mobile.git](https://github.com/teu-utilizador/atec-mobile.git)
    ```

2.  **Configurar o Endereço IP:**
    Como o Android Emulator não entende `localhost`, é necessário definir o IP correto no ficheiro `RetrofitClient.kt`:
    * **Se usares Emulador:** O IP deve ser `10.0.2.2`.
    * **Se usares Telemóvel Físico:** Usa o IP da tua máquina na rede local (ex: `192.168.1.X`).

    ```kotlin
    // Exemplo em RetrofitClient.kt
    private const val BASE_URL = "[http://10.0.2.2:3000/api/](http://10.0.2.2:3000/api/)" 
    ```

3.  **Compilar e Correr:**
    * Abre o projeto no Android Studio.
    * Sincroniza o Gradle.
    * Clica no botão **Run (▶)**.

---

## :open_file_folder: Estrutura do Projeto
com.example.a609_projetofinal ├── :open_file_folder: data │ ├── Modelos.kt # Data Classes (User, Curso, Horario, etc.) │ └── RetrofitClient.kt # Configuração da API ├── :open_file_folder: ui │ └── theme # Cores e Estilos ├── ApiService.kt # Endpoints da API (GET, POST) ├── LoginActivity.kt # Lógica de Login ├── TwoFactorActivity.kt # Validação de Código Email └── HomeActivity.kt # Dashboard Principal e Calendário

---

## :man_technologist: Autor

Desenvolvido por **Catarina Tomaz**.
