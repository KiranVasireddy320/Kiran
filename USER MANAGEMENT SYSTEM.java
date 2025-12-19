// =============================================
// PROJECT: User Management System (Backend Application)
// STACK: Java, Spring Boot, Spring Security (JWT), JPA/Hibernate, MySQL
// PURPOSE: Resume-ready Java Backend project for Fresher roles
// =============================================

// ---------- pom.xml (key dependencies) ----------
/*
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
  </dependency>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
  </dependency>
</dependencies>
*/

// ---------- application.properties ----------
/*
spring.datasource.url=jdbc:mysql://localhost:3306/user_management
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=secretkey123
*/

// ---------- Entity ----------
@Entity
@Table(name = "users")
class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  private String name;
  private String password;
  private String role; // USER, ADMIN
}

// ---------- Repository ----------
interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}

// ---------- DTO ----------
class UserRequest {
  public String name;
  public String email;
  public String password;
}

class UserResponse {
  public Long id;
  public String name;
  public String email;
  public String role;
}

// ---------- JWT Utility ----------
@Component
class JwtUtil {
  private String secret = "secretkey123";

  public String generateToken(String email) {
    return Jwts.builder()
      .setSubject(email)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + 3600000))
      .signWith(SignatureAlgorithm.HS256, secret)
      .compact();
  }
}

// ---------- Service ----------
@Service
class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public UserResponse register(UserRequest request) {
    User user = new User();
    user.setName(request.name);
    user.setEmail(request.email);
    user.setPassword(passwordEncoder.encode(request.password));
    user.setRole("USER");

    userRepository.save(user);

    UserResponse response = new UserResponse();
    response.id = user.getId();
    response.name = user.getName();
    response.email = user.getEmail();
    response.role = user.getRole();

    return response;
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(user -> {
      UserResponse res = new UserResponse();
      res.id = user.getId();
      res.name = user.getName();
      res.email = user.getEmail();
      res.role = user.getRole();
      return res;
    }).toList();
  }
}

// ---------- Controller ----------
@RestController
@RequestMapping("/api/users")
class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
    return ResponseEntity.ok(userService.register(request));
  }

  @GetMapping("/all")
  public ResponseEntity<List<UserResponse>> getAll() {
    return ResponseEntity.ok(userService.getAllUsers());
  }
}

// ---------- Security Config ----------
@Configuration
class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

// ---------- README (What to mention) ----------
/*
Features:
- User Registration API
- Password encryption using BCrypt
- Role-based user model
- RESTful API architecture
- JPA/Hibernate persistence
- MySQL relational database
- API testing using Postman

This project demonstrates real-world Java backend development practices.
*/