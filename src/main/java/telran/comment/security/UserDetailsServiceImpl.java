package telran.comment.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import telran.comment.configuration.KafkaConsumer;
import telran.comment.dto.accounting.ProfileDto;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService {
    final KafkaConsumer kafkaConsumer;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String encryptedEmail = EmailEncryptionConfiguration.encryptAndEncodeUserId(email);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (profile != null && encryptedEmail.equals(profile.getEmail())) {
            return new User(profile.getEmail(), profile.getPassword(), AuthorityUtils.createAuthorityList(profile.getRoles()));
        } else throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong email");
    }
}
