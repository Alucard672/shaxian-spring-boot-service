package com.shaxian.service.settings;

import com.shaxian.entity.StoreInfo;
import com.shaxian.repository.StoreInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class StoreInfoService {

    private final StoreInfoRepository storeInfoRepository;

    public StoreInfoService(StoreInfoRepository storeInfoRepository) {
        this.storeInfoRepository = storeInfoRepository;
    }

    public StoreInfo getStoreInfo() {
        Optional<StoreInfo> storeInfo = storeInfoRepository.findFirstByOrderByIdAsc();
        if (storeInfo.isEmpty()) {
            StoreInfo newStore = new StoreInfo();
            newStore.setName("");
            return storeInfoRepository.save(newStore);
        }
        return storeInfo.get();
    }

    @Transactional
    public StoreInfo updateStoreInfo(Map<String, Object> request) {
        Optional<StoreInfo> existing = storeInfoRepository.findFirstByOrderByIdAsc();
        StoreInfo storeInfo;

        if (existing.isPresent()) {
            storeInfo = existing.get();
        } else {
            storeInfo = new StoreInfo();
        }

        if (request.containsKey("name")) storeInfo.setName((String) request.get("name"));
        if (request.containsKey("code")) storeInfo.setCode((String) request.get("code"));
        if (request.containsKey("address")) storeInfo.setAddress((String) request.get("address"));
        if (request.containsKey("phone")) storeInfo.setPhone((String) request.get("phone"));
        if (request.containsKey("email")) storeInfo.setEmail((String) request.get("email"));
        if (request.containsKey("fax")) storeInfo.setFax((String) request.get("fax"));
        if (request.containsKey("postalCode")) storeInfo.setPostalCode((String) request.get("postalCode"));
        if (request.containsKey("remark")) storeInfo.setRemark((String) request.get("remark"));

        return storeInfoRepository.save(storeInfo);
    }
}
