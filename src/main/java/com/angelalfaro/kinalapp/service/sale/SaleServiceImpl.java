package com.angelalfaro.kinalapp.service.sale;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angelalfaro.kinalapp.entity.Sale;
import com.angelalfaro.kinalapp.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SaleServiceImpl implements ISaleService{
    
    private final SaleRepository saleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Sale> listAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public List<Sale> listAllByState(int state) {
        return saleRepository.findByStateSale(state, Pageable.unpaged()).toList();
    }

    @Override
    public Sale saveSale(Sale sale) {
        validateSale(sale);

        if (sale.getStateSale() == 0) 
            sale.setStateSale(1);

        return saleRepository.save(sale);
        
    }

    @Override
    public Optional<Sale> findByCodeSale(Long codeSale) {
        return saleRepository.findById(codeSale);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Sale updateSale(Long codeSale, Sale sale) {
        if (!saleRepository.existsById(codeSale)) {
            throw new RuntimeException("La venta no fue encontrada");
        }

        //This instance will used to avoid have empty attributes
        //if you don't send new attributes
        Optional<Sale> s = saleRepository.findById(codeSale);

        //Set the last total if you don't send a new total
        sale.setTotalSale(sale.getTotalSale().signum() <= 0
            ? sale.getTotalSale() : s.get().getTotalSale() );

        //Set the last state if you don't send a new state
        sale.setStateSale(sale.getStateSale() <= 0
            ? sale.getStateSale() : s.get().getStateSale() );

        sale.setSaleDateSale(s.get().getSaleDateSale());

        return saleRepository.save(sale);

    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSale(Long codeSale) {
        if (!saleRepository.existsById(codeSale)) {
            throw new RuntimeException("La venta no fue encontrada");
        }

        saleRepository.deleteById(codeSale);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeSale(Long codeSale) {
        return saleRepository.existsById(codeSale);
    }

    public void validateSale(Sale sale){
        if (sale.getTotalSale().signum() <= 0) {
            throw new IllegalArgumentException("El total es obligatorio y no puede ser menor o igual a 0");
        }
    }
    
}
