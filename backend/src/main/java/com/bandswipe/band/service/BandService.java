package com.bandswipe.band.service;

import com.bandswipe.auth.entity.User;
import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.band.dto.BandResponse;
import com.bandswipe.band.dto.CreateBandRequest;
import com.bandswipe.band.entity.Band;
import com.bandswipe.band.entity.BandMember;
import com.bandswipe.band.repository.BandMemberRepository;
import com.bandswipe.band.repository.BandRepository;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;
    private final BandMemberRepository bandMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public BandResponse createBand(UUID creatorId, CreateBandRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Band band = Band.builder()
                .name(request.getName())
                .genre(request.getGenre())
                .bio(request.getBio())
                .createdBy(creator)
                .build();

        band = bandRepository.save(band);

        // Add creator as first member
        BandMember creatorMember = BandMember.builder()
                .band(band)
                .user(creator)
                .role("Creator")
                .build();
        band.getMembers().add(creatorMember);

        // Add additional members if provided
        if (request.getMemberUserIds() != null) {
            for (UUID memberId : request.getMemberUserIds()) {
                if (memberId.equals(creatorId)) continue;
                User memberUser = userRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + memberId));
                BandMember member = BandMember.builder()
                        .band(band)
                        .user(memberUser)
                        .role("Member")
                        .build();
                band.getMembers().add(member);
            }
        }

        band = bandRepository.save(band);
        return BandResponse.fromEntity(band);
    }

    @Transactional(readOnly = true)
    public List<BandResponse> getUserBands(UUID userId) {
        return bandRepository.findBandsByUserId(userId).stream()
                .map(BandResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public BandResponse getBand(UUID bandId, UUID userId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));

        boolean isMember = band.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(userId));
        if (!isMember) {
            throw new ResourceNotFoundException("Band not found");
        }

        return BandResponse.fromEntity(band);
    }

    @Transactional
    public BandResponse addMember(UUID bandId, UUID requesterId, UUID newUserId, String role) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));

        boolean isRequesterMember = band.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(requesterId));
        if (!isRequesterMember) {
            throw new ResourceNotFoundException("Band not found");
        }

        if (bandMemberRepository.existsByBandIdAndUserId(bandId, newUserId)) {
            throw new IllegalStateException("User is already a member of this band");
        }

        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BandMember member = BandMember.builder()
                .band(band)
                .user(newUser)
                .role(role != null ? role : "Member")
                .build();
        band.getMembers().add(member);

        band = bandRepository.save(band);
        return BandResponse.fromEntity(band);
    }

    @Transactional
    public void removeMember(UUID bandId, UUID requesterId, UUID targetUserId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));

        boolean isRequesterCreator = band.getCreatedBy().getId().equals(requesterId);
        boolean isSelfRemoval = requesterId.equals(targetUserId);

        if (!isRequesterCreator && !isSelfRemoval) {
            throw new IllegalStateException("Only the band creator can remove members");
        }

        BandMember member = bandMemberRepository.findByBandIdAndUserId(bandId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        band.getMembers().remove(member);
        bandRepository.save(band);
    }
}
