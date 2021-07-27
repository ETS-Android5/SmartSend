import React from 'react'
import { ServicesContainer, ServicesWrapper, ServicesH1, ServicesCard, ServicesIcon, ServicesH2, ServicesP } from './seviceselements'
import Icon1 from '../../images/svg-1.svg'
import Icon2 from '../../images/svg-2.svg'
import Icon3 from '../../images/svg-3.svg'

const Services = () => {
    return (
        <ServicesContainer id="services">
            <ServicesH1>Our Services</ServicesH1>
            <ServicesWrapper>
                <ServicesCard>
                    <ServicesIcon src={Icon1} />
                    <ServicesH2>Services Header</ServicesH2>
                    <ServicesP>
                        Services paragraph describes the service.
                    </ServicesP>
                </ServicesCard>

                <ServicesCard>
                    <ServicesIcon src={Icon2} />
                    <ServicesH2>Services Header</ServicesH2>
                    <ServicesP>
                        Services paragraph describes the service.
                    </ServicesP>
                </ServicesCard>

                <ServicesCard>
                    <ServicesIcon src={Icon3} />
                    <ServicesH2>Services Header</ServicesH2>
                    <ServicesP>
                        Services paragraph describes the service.
                    </ServicesP>
            </ServicesCard>
            </ServicesWrapper>
        </ServicesContainer>
    )
}

export default Services
