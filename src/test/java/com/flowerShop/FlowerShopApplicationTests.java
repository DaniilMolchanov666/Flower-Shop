package com.flowerShop;

import com.flowerShop.model.Product;
import com.flowerShop.repository.ProductsRepository;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
public class FlowerShopApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductsRepository mockRep;

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName
            .parse("mirror.gcr.io/postgres:alpine3.19")
            .asCompatibleSubstituteFor("postgres"))
            .withImagePullPolicy(PullPolicy.alwaysPull())
            .withDatabaseName("cottonpads")
            .withUsername("daniilmolchanov")
            .withPassword("microcuts1928")
            .withInitScript("script.sql");

    @Container
    private static final GenericContainer<?> minioContainer =
            new GenericContainer<>(DockerImageName
                    .parse("mirror.gcr.io/minio/minio")
                    .asCompatibleSubstituteFor("minio"))
                    .withEnv("MINIO_ROOT_USER", "minioadmin")
                    .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
                    .withCommand("server", "/data")
                    .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                            new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(9010),
                                    new ExposedPort(9010)))));


    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("minio.accessKey", minioContainer::getEnv);
        registry.add("minio.secretKey", minioContainer::getEnv);
    }

    @BeforeEach
    void testIsRunning() throws Exception {
        postgresContainer.isRunning();
        minioContainer.isRunning();
        mockMvc.perform(post("http://localhost:8080/login")
                        .param("username", "procvetanie_shop")
                        .param("password", "cottonpads1928"))
                .andExpect(redirectedUrl("/api/products"));
    }

    public static RequestPostProcessor userHttpBasic() {
        return SecurityMockMvcRequestPostProcessors
                .httpBasic("procvetanie_shop", "cottonpads1928");
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @Test
    public void testLoginPageWork() throws Exception {
        mockMvc.perform(post("http://localhost:8080/login")
                        .param("username", "procvetanie_shop")
                        .param("password", "cottonpads1928"))
                .andExpect(redirectedUrl("/api/products"));
    }

    @Test
    @DisplayName("Тест на отображение продуктов на главной странице")
    public void testOfShowAllProduct() throws Exception {
        mockMvc.perform(post("http://localhost:8080/api/products/delete/Цветы"))
                .andExpect(redirectedUrl("/api/products"))
                .andExpect(status().is(302));
        var faker = new Faker();
        var product = Instancio.of(Product.class)
                .ignore(field(Product::getId))
                .supply(all(Product.class), random -> Product.builder()
                        .name(faker.lorem().paragraph())
                        .price(faker.number().digit())
                        .category(random.oneOf("Цветы", "Монобукет", "Композиция", "Составной букет", "Другое"))
                        .purchasePrice(faker.number().digit())
                        .description(faker.lorem().paragraph())
                        .build())
                .create();
    }

    @Test
    @DisplayName("Тест на добавление продукта")
    @WithMockUser(username = "procvetanie_shop", password = "cottonpads1928")
    public void test() throws Exception {
        Faker faker = new Faker();
        var product = Instancio.of(Product.class)
                .ignore(field(Product::getId))
                .supply(all(Product.class), random -> Product.builder()
                        .name(faker.lorem().paragraph())
                        .price(faker.number().digit())
                        .category(random.oneOf("Цветы", "Монобукет", "Композиция", "Составной букет", "Другое"))
                        .purchasePrice(faker.number().digit())
                        .description(faker.lorem().paragraph())
                        .build())
                .create();

        mockMvc.perform(post("http://localhost:8080/api/products/create?")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", product.getName())
                        .param("category", product.getCategory())
                        .param("description", product.getDescription())
                        .param("price", product.getPrice())
                        .param("purchase_price", product.getPurchasePrice()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест на удаление продукта")
    public void testOfDeleteProduct() throws Exception {
        String testingProductName = "Цветы";
        String notExistingProductName = "Какой-то продукт";
        mockMvc.perform(post("http://localhost:8080/api/products/delete/" + testingProductName))
                .andExpect(redirectedUrl("/api/products"))
                .andExpect(status().is(302));

    }

    @Test
    @DisplayName("Тест на обновление продукта")
    public void testOfUpdateProduct() throws Exception {
        mockMvc.perform(post("http://localhost:8080/api/products/update/Цветы"))
                .andExpect(redirectedUrl("/api/products"))
                .andExpect(status().is(302));
    }
}
