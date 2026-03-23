package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() throws MyCheckedException {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow).isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception 을 상속받은 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * 외부 클래스 인스턴스 상태가 필요하다면 내부 클래스.
     * 상태 필요 없이, 유틸리티적인 기능만 한다 - 내부 정적 클래스
     *
     * Checked 예외는
     * 예외를 잡아서 처리하거나 던져야 함
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 스택 트레이스는 마지막에 그냥 e 던지면 됨.
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 체크 예외를 밖으로 던지는 코드. throws
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
       public void call() throws MyCheckedException {
           throw new MyCheckedException("ex");
       }
    }
}
