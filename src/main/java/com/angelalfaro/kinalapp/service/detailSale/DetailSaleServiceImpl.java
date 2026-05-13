package com.angelalfaro.kinalapp.service.detailSale;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angelalfaro.kinalapp.entity.DetailSale;
import com.angelalfaro.kinalapp.repository.DetailSaleRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DetailSaleServiceImpl implements IDetailSaleService{

    private final DetailSaleRepository detailSaleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetailSale> listAllDetailSale() {
        return detailSaleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetailSale> listAllByState(int state) {
        return detailSaleRepository.findByStateDetailSale(state, Pageable.unpaged())
                .toList();
    }

    @Override
    @Transactional
    public DetailSale saveDetailSale(DetailSale detailSale) {
        validateDetailSale(detailSale);

        if (detailSale.getStateDetailSale() == 0) 
            detailSale.setStateDetailSale(1);

        return detailSaleRepository.save(detailSale);
        
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetailSale> findByCodeDetailSale(Long codeDetailSale) {
        return detailSaleRepository.findById(codeDetailSale);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DetailSale updateDetailSale(Long codeDetailSale, DetailSale detailSale) {
        if (!detailSaleRepository.existsById(codeDetailSale)) {
            throw new RuntimeException("El Detalle de Venta no fue encontrado");
        }

        //This instance will used to avoid have empty attributes
        //if you don't send new attributes
        Optional<DetailSale> dS = detailSaleRepository.findById(codeDetailSale);

        //Set the last amount if you don't send a new amount
        detailSale.setAmountDetailSale(detailSale.getAmountDetailSale() <= 0
                ? detailSale.getAmountDetailSale() : dS.get().getAmountDetailSale());

        //Set the last unit price if you don't send a new unit price
        detailSale.setUnitPriceDetailSale(detailSale.getUnitPriceDetailSale() == null ||
            detailSale.getUnitPriceDetailSale().signum() <= 0
                ? detailSale.getUnitPriceDetailSale() : dS.get().getUnitPriceDetailSale());

        //Set the last subtotal if you don't send a new subtotal
        detailSale.setSubtotal(detailSale.getSubtotal() == null ||
            detailSale.getSubtotal().signum() <= 0
                ? detailSale.getSubtotal() : dS.get().getSubtotal());

        //Set the last state if you don't send a new state
        detailSale.setStateDetailSale(detailSale.getStateDetailSale() <= 0
            ? detailSale.getStateDetailSale() : dS.get().getStateDetailSale() );

        validateDetailSale(detailSale);

        return detailSaleRepository.save(detailSale);

    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDetailSale(Long codeDetailSale) {
        if (!detailSaleRepository.existsById(codeDetailSale)) {
            throw new RuntimeException("El Detalle de Venta no fue encontrado");
        }

        detailSaleRepository.deleteById(codeDetailSale);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeDetailSale(Long codeDetailSale) {
        return detailSaleRepository.existsById(codeDetailSale);
    }

    public void validateDetailSale(DetailSale detailSale){
        if (detailSale.getAmountDetailSale() <= 0){
            throw new IllegalArgumentException("La cantidad es obligatoria, tiene que ser mayor a 0");
        }
        if ( detailSale.getUnitPriceDetailSale() == null || detailSale.getUnitPriceDetailSale().signum() <= 0){
            throw new IllegalArgumentException("El precio unitario es obligatorio, tiene que ser mayor a 0");
        }
        if ( detailSale.getSubtotal() == null || detailSale.getSubtotal().signum() <= 0){
            throw new IllegalArgumentException("El sub total es obligatorio, tiene que ser mayor a 0");
        }
    }
    
}
