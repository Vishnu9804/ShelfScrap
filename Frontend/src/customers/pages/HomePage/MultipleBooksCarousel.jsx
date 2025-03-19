import Slider from 'react-slick';
import { bestSellers } from '../../../data/bestSellers';
import Carouselitems from './Carouselitems';
import React from 'react';

const MultipleBooksCarousel = () => {
  const settings = {
    dots: false,
    infinite: true,
    slidesToShow: 4,
    slidesToScroll: 1,
    autoplay: true,
    speed: 1000,
    autoplaySpeed: 1000,
    arrows: false
  };
  return (
    <div>
      <Slider {...settings}>
        {bestSellers.map((book) => <Carouselitems image={book.image} title={book.title} />)}
      </Slider>
    </div>
  )
}

export default MultipleBooksCarousel