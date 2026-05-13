package com.shaxian.biz.appservice.admin;

import com.shaxian.biz.dto.admin.request.UpdatePackageRequest;
import com.shaxian.biz.dto.admin.response.PackageVO;
import com.shaxian.biz.entity.TenantPackage;
import com.shaxian.biz.repository.TenantPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminPackageAppService {

    private final TenantPackageRepository packageRepository;

    public AdminPackageAppService(TenantPackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<PackageVO> listAll() {
        return packageRepository.findAll().stream()
                .map(PackageVO::from)
                .collect(Collectors.toList());
    }

    public PackageVO get(Long id) {
        return packageRepository.findById(id)
                .map(PackageVO::from)
                .orElseThrow(() -> new IllegalArgumentException("套餐不存在"));
    }

    @Transactional
    public PackageVO update(Long id, UpdatePackageRequest req) {
        TenantPackage p = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("套餐不存在"));

        if (req.getName() != null && !req.getName().isBlank()) {
            // 名称唯一性校验
            Optional<TenantPackage> dup = packageRepository.findByName(req.getName());
            if (dup.isPresent() && !dup.get().getId().equals(id)) {
                throw new IllegalArgumentException("套餐名已存在");
            }
            p.setName(req.getName());
        }
        if (req.getConcurrentLimit() != null) p.setConcurrentLimit(req.getConcurrentLimit());
        if (req.getYearlyPrice() != null) p.setYearlyPrice(req.getYearlyPrice());
        if (req.getStatus() != null) {
            try {
                p.setStatus(TenantPackage.PackageStatus.valueOf(req.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的状态值: " + req.getStatus());
            }
        }
        return PackageVO.from(packageRepository.save(p));
    }
}
