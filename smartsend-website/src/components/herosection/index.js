import React, {useState} from 'react'
import Video from '../../videos/video.mp4'
import { HeroContainer, HeroBg, VideoBg, HeroContent, HeroH1, HeroP, HeroBtnWrapper, ArrowForward, ArrowRight } from './heroelements'
import {Button} from '../buttonelement'

const HeroSection = () => {
    const [hover, setHover] = useState(false)

    const onHover = () => {
        setHover(!hover)
    }

    const sendMail = () => {
        window.location.assign("mailto:il.smartsend@gmail.com?subject=Inquery About SmartSend Services&body=Hello! I would like to get more information about SmartSend services.");
    }

    return (
        <HeroContainer>
            <HeroBg>
                <VideoBg autoPlay loop muted src={Video} type='video/mp4' />
            </HeroBg>
            <HeroContent>
                <HeroH1>Distribution Service Made Easy </HeroH1>
                <HeroP>
                Join us today and start enjoying the benefits of SmartSend services.
                </HeroP>
                <HeroBtnWrapper>
                    <Button onClick={sendMail} onMouseEnter={onHover} onMouseLeave={onHover} primary='true' dark='true' >
                        More Info { hover ? <ArrowForward/> : <ArrowRight/>}
                    </Button>
                </HeroBtnWrapper>
            </HeroContent>
        </HeroContainer>
    )
}

export default HeroSection
