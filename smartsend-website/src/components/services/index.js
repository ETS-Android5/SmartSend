import React from 'react'
import { ServicesContainer, ServicesWrapper, ServicesH1, ServicesCard, ServicesIcon, ServicesH2, ServicesP } from './seviceselements'
import Icon1 from '../../images/car_watch.png'
import Icon2 from '../../images/scales.png'
import Icon3 from '../../images/timer.png'

const Services = () => {
    return (
        <ServicesContainer id="services">
            <ServicesH1>Our Services</ServicesH1>
            <ServicesWrapper>
                <ServicesCard>
                    <ServicesIcon src={Icon1} />
                    <ServicesH2>Call for a Courier</ServicesH2>
                    <ServicesP>
                        Cannot receive your delivery? Let us send it to you with a mail guy.
                    </ServicesP>
                </ServicesCard>

                <ServicesCard>
                    <ServicesIcon src={Icon2} />
                    <ServicesH2>Delivery Price</ServicesH2>
                    <ServicesP>
                        Our prices depend on the size of your parcel.
                    </ServicesP>
                </ServicesCard>

                <ServicesCard>
                    <ServicesIcon src={Icon3} />
                    <ServicesH2>Delivery Term</ServicesH2>
                    <ServicesP>
                    ​   All deliveries are carried within several days.
                    </ServicesP>
            </ServicesCard>
            </ServicesWrapper>
        </ServicesContainer>
    )
}

export default Services
