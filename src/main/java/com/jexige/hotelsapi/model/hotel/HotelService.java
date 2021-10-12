package com.jexige.hotelsapi.model.hotel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Optional<Hotel> findById(long id) {
        return hotelRepository.findById(id);
    }

    public Page<Hotel> findAll(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }

    public Hotel update(Hotel newHotel, long id) throws HotelNotFoundException {
        final Optional<Hotel> optionalHotel = hotelRepository.findById(id);
        if (optionalHotel.isEmpty()) {
            throw new HotelNotFoundException(id);
        } else {
            return save(newHotel);
        }
    }

    public Hotel save(Hotel newHotel) {
        return hotelRepository.save(newHotel);
    }

    public void deleteById(long id) throws HotelNotFoundException {
        final Optional<Hotel> opt = findById(id);
        if (opt.isEmpty()) {
            throw new HotelNotFoundException(id);
        } else {
            hotelRepository.deleteById(id);
        }
    }

}
