package hello.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_2 {

    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    // TransactionTemplate은 클래스니까, 유연성을 확보하기 위해 PlatformTransactionManger 인터페이스를 외부 주입한다.
    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) { // 람다에서는 체크 예외를 던질 수 없음.
                throw new IllegalStateException(e);
            }
        });
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById( fromId);
        Member toMember = memberRepository.findById( toId);

        memberRepository.update( fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                // 커넥션이 다시 풀로 돌아가면, autoCommit 값 또한 false 로 전달된다.
                // 자동 커밋 모드를 가정하기 때문에, true 로 변경한다.
                con.setAutoCommit(true); // 커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

}
