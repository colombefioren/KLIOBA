package school.hei.klioba.endpoint.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

class ErrorHandlerTest {

  private ErrorHandler errorHandler;
  private Model model;

  @BeforeEach
  void setUp() {
    errorHandler = new ErrorHandler();
    model = mock(Model.class);
  }

  @Test
  void handleException_setsErrorMessageAndReturnsErrorView() {
    var exception = new RuntimeException("Test error message");

    var result = errorHandler.handleException(exception, model);

    assertEquals("error", result);
    verify(model).addAttribute("errorMessage", "Test error message");
  }

  @Test
  void handleException_withNullMessage_setsNullErrorMessage() {
    var exception = new RuntimeException((String) null);

    var result = errorHandler.handleException(exception, model);

    assertEquals("error", result);
    verify(model).addAttribute("errorMessage", null);
  }

  @Test
  void handleException_withDifferentExceptionTypes_handlesAll() {
    var illegalArgException = new IllegalArgumentException("Invalid argument");

    var result = errorHandler.handleException(illegalArgException, model);

    assertEquals("error", result);
    verify(model).addAttribute("errorMessage", "Invalid argument");
  }
}
