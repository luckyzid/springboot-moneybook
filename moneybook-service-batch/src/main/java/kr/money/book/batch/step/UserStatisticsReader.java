package kr.money.book.batch.step;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kr.money.book.batch.entity.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class UserStatisticsReader implements ItemReader<UserDto> {

    private Iterator<UserDto> userIterator;

    @Override
    public UserDto read() {
        if (userIterator == null) {
            // 실제로는 DB에서 사용자 목록을 가져와야 하지만, 테스트를 위해 더미 데이터를 생성합니다.
            List<UserDto> users = createDummyUsers();
            userIterator = users.iterator();
            log.info("Found {} users to process", users.size());
        }
        
        if (userIterator.hasNext()) {
            return userIterator.next();
        }
        
        return null;
    }
    
    private List<UserDto> createDummyUsers() {
        List<UserDto> users = new ArrayList<>();
        
        users.add(UserDto.builder().userKey("user-1").name("사용자1").email("user1@example.com").build());
        users.add(UserDto.builder().userKey("user-2").name("사용자2").email("user2@example.com").build());
        users.add(UserDto.builder().userKey("user-3").name("사용자3").email("user3@example.com").build());
        
        return users;
    }
} 