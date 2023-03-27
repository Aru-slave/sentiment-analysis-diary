package com.sentimentdiary.demo.member;

import com.sentimentdiary.demo.auth.utils.CustomAuthorityUtils;
import com.sentimentdiary.demo.exception.BusinessLogicException;
import com.sentimentdiary.demo.exception.ExceptionCode;
import com.sentimentdiary.demo.helper.event.MemberRegistrationApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;


    public Member createMember(Member member) {
        verifyExistsEmail(member.getEmail());

        Member newMember = new Member();

        String encryptedPassword = passwordEncoder.encode(member.getPw());  //패스워드 인코딩
        newMember.setPw(encryptedPassword);
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        newMember.setRoles(roles);
        newMember.setNickName(member.getNickName());
        newMember.setEmail(member.getEmail());
        newMember.setGoogle(false);
        Member savedMember = memberRepository.save(newMember);

        publisher.publishEvent(new MemberRegistrationApplicationEvent(this, savedMember));
        return savedMember;
    }

    public Member updateMember(Member member) {
        if (getLoginMember() == null)
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);


        Member findMember = findVerifiedMember(getLoginMember().getMemberId());
        String name = findMember.getNickName();
        if (member.getPw() != null) {
            String encryptedPassword = passwordEncoder.encode(member.getPw());
            Optional.ofNullable(member.getPw())
                    .ifPresent(pw -> findMember.setPw(encryptedPassword));
        }
        Optional.ofNullable(member.getNickName())
                .ifPresent(findMember::setNickName);
        if(!Objects.equals(name, findMember.getNickName())){
        }

        return memberRepository.save(findMember);
    }

    public Member findMember(long memberId) {
        return findVerifiedMember(memberId);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public void deleteMember(long memberId) {
        Member findMember = findVerifiedMember(memberId);

        memberRepository.delete(findMember);
    }

    public Member findByNiceName(String name) {
        Optional<Member> optionalMembers =
                memberRepository.findByNickName(name);
        return optionalMembers.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    //로그인한 회원정보 가져오기
    public Member getLoginMember() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();  //SecurityContextHolder에서 회원정보 가져오기
        Optional<Member> optionalMember;

        if (principal.toString().contains("@"))
            optionalMember = memberRepository.findByEmail(principal.toString());
        else
            optionalMember = memberRepository.findByNickName(principal.toString());

        return optionalMember.orElse(null);
    }

    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);
        return optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }


    public Boolean check(MemberDto.Check check) {
        if (check.getNickName() != null) {
            if (memberRepository.findByNickName(check.getNickName()).isPresent()) {
                throw new BusinessLogicException(ExceptionCode.NICKNAME_EXIST);
            }
        }
        if (check.getEmail() != null) {
            if (memberRepository.findByEmail(check.getEmail()).isPresent()) {
                throw new BusinessLogicException(ExceptionCode.EMAIL_EXIST);
            }
        }

        return true;
    }


}