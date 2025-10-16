package com.crm.service;

import com.crm.dto.SellerDto;
import com.crm.dto.AnalyticsDto;
import com.crm.entity.Seller;
import com.crm.repository.SellerRepository;
import com.crm.exception.ResourceNotFoundException;
import com.crm.exception.ValidationException;
import com.crm.exception.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    public List<SellerDto> getAllSellers() {
        try {
            return sellerRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка продавцов: " + e.getMessage());
        }
    }

    public Optional<SellerDto> getSellerById(Long id) {
        if (id == null) {
            throw ValidationException.forInvalidId();
        }

        return sellerRepository.findById(id)
                .map(this::convertToDto);
    }

    public SellerDto createSeller(SellerDto sellerDto) {
        // Валидация
        if (sellerDto.getName() == null || sellerDto.getName().trim().isEmpty()) {
            throw ValidationException.forEmptyName();
        }

        // Проверка на дубликат по имени
        String normalizedName = sellerDto.getName().trim();
        sellerRepository.findByName(normalizedName).ifPresent(seller -> {
            throw DuplicateResourceException.forSeller(normalizedName);
        });

        Seller seller = new Seller();
        seller.setName(normalizedName);
        seller.setContactInfo(sellerDto.getContactInfo());

        Seller savedSeller = sellerRepository.save(seller);
        return convertToDto(savedSeller);
    }

    public Optional<SellerDto> updateSeller(Long id, SellerDto sellerDto) {
        return sellerRepository.findById(id)
                .map(existingSeller -> {
                    if (sellerDto.getName() == null || sellerDto.getName().trim().isEmpty()) {
                        throw ValidationException.forEmptyName();
                    }

                    String normalizedName = sellerDto.getName().trim();

                    // Проверка на дубликат (исключая текущего продавца)
                    sellerRepository.findByName(normalizedName)
                            .ifPresent(seller -> {
                                if (!seller.getId().equals(id)) {
                                    throw DuplicateResourceException.forSeller(normalizedName);
                                }
                            });

                    existingSeller.setName(normalizedName);
                    existingSeller.setContactInfo(sellerDto.getContactInfo());
                    Seller updatedSeller = sellerRepository.save(existingSeller);
                    return convertToDto(updatedSeller);
                });
    }

    public boolean deleteSeller(Long id) {
        // Находим продавца - если не найден, выбрасываем исключение
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forSeller(id));

        // Проверяем есть ли связанные транзакции
        if (!seller.getTransactions().isEmpty()) {
            // ИСПРАВЛЕНО: убрана лишняя точка с запятой и использован правильный метод
            throw ValidationException.forSellerWithTransactions();
        }

        sellerRepository.delete(seller);
        return true;
    }

    public List<AnalyticsDto.SellerWithTotal> getSellersWithTotalLessThan(LocalDateTime startDate,
                                                                          LocalDateTime endDate,
                                                                          BigDecimal minAmount) {
        List<Object[]> results = sellerRepository.findSellersWithTotalLessThan(startDate, endDate, minAmount);
        return results.stream()
                .map(result -> new AnalyticsDto.SellerWithTotal(
                        (Long) result[0],
                        (String) result[1],
                        (BigDecimal) result[2]
                ))
                .collect(Collectors.toList());
    }

    private SellerDto convertToDto(Seller seller) {
        return new SellerDto(
                seller.getId(),
                seller.getName(),
                seller.getContactInfo(),
                seller.getRegistrationDate()
        );
    }
}