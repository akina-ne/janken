package oit.is.z2974.kaizi.janken.config;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupLogUsers implements ApplicationRunner {
  private final ApplicationContext ctx;
  private final Logger logger = LoggerFactory.getLogger(StartupLogUsers.class);

  public StartupLogUsers(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public void run(ApplicationArguments args) {
    logger.info("----- StartupLogUsers: checking InMemoryUserDetailsManager beans -----");

    Map<String, InMemoryUserDetailsManager> beans = ctx.getBeansOfType(InMemoryUserDetailsManager.class);
    if (beans.isEmpty()) {
      logger.warn("No InMemoryUserDetailsManager beans found in ApplicationContext.");
      return;
    }

    logger.info("Found {} InMemoryUserDetailsManager bean(s): {}", beans.size(), beans.keySet());
    for (Map.Entry<String, InMemoryUserDetailsManager> e : beans.entrySet()) {
      String beanName = e.getKey();
      InMemoryUserDetailsManager mgr = e.getValue();
      logger.info("Inspecting bean '{}': class={}", beanName, mgr.getClass().getName());

      try {
        Field usersField = null;
        Class<?> cls = mgr.getClass();
        while (cls != null && usersField == null) {
          try {
            usersField = cls.getDeclaredField("users");
          } catch (NoSuchFieldException ex) {
            cls = cls.getSuperclass();
          }
        }
        if (usersField == null) {
          logger.warn("Could not find 'users' field on InMemoryUserDetailsManager implementation");
          continue;
        }
        usersField.setAccessible(true);
        Object usersObj = usersField.get(mgr);
        if (usersObj instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, ?> usersMap = (Map<String, ?>) usersObj;
          Set<String> names = usersMap.keySet();
          logger.info("Bean '{}' has {} registered usernames: {}", beanName, names.size(), names);
        } else {
          logger.warn("'users' field is not a Map: {}", usersObj);
        }
      } catch (Exception ex) {
        logger.error("Failed to inspect users map for bean '{}': {}", beanName, ex.toString(), ex);
      }
    }

    logger.info("----- End StartupLogUsers -----");
  }
}
