package mariposas.constant;

public class AppConstant {
    public static final String USER_ALREADY_EXISTS = "Já existe um usuário cadastrado para o e-mail informado";
    public static final String SAME_EMAIL = "O e-mail informado é o mesmo que o e-mail atual";
    public static final String EMAIL_ANOTHER_USER = "O e-mail informado está associado a outra conta";
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String SAVE_IMAGE_ERROR = "Error saving image to bucket";

    public static final String USER_CREATED = "Cadastro criado com sucesso. Agora, você é uma mariposa!";
    public static final String LOGIN_SUCCESS = "Acesso concedido";
    public static final String LOGIN_FAIL = "Acesso não permitido";
    public static final String PROFILE_SUCCESS = "Perfil atualizado com sucesso";
    public static final String FORGOT_PASSWORD_SUCCESS = "Se o e-mail existir na base, um link de redefinição de senha será enviado ao seu e-mail!";

    public static final String BUCKET = "mariposas-digitais";
    public static final String BUCKET_PATH = "profile-images/";
    public static final String IMAGE_NAME = "MD_IMAGE_%s.png";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String EMAIL = "email";

    public static final String QUERY_GET_MENTEES = "SELECT u.name, u.email, u.phone, u.profile, " +
            "u.age, ml.\"level\" AS menteeLevel, u.image " +
            "FROM public.users u JOIN public.mentees m ON u.id = m.user_id " +
            "JOIN mentee_level ml ON m.mentee_level_id  = ml.id " +
            "WHERE m.is_sponsored  = false";
    public static final String QUERY_COUNT_MENTEES = "SELECT COUNT(*) FROM public.mentees";
}