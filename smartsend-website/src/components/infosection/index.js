import React from 'react'
import {Button} from '../buttonelement'
import { InfoContainer, InfoWrapper, Column1, TextWrapper, TopLine, Heading, InfoRow, Subtitle, BtnWrap, Column2, ImgWrap, Img } from './infoelements'

const InfoSection = ({
    lightBg,
    id,
    lightText,
    topLine,
    darkText, 
    description, 
    buttonLabel, 
    headLine, 
    alt,
    img,
    primary,
    dark,
    dark2, 
    imgStart}) => {
    return (
        <>
          <InfoContainer lightBg={lightBg} id={id}>
              <InfoWrapper>
                  <InfoRow imgStart={imgStart}>
                      <Column1>
                        <TextWrapper>
                        <TopLine>{topLine}</TopLine>
                        <Heading lightText={lightText}>{headLine}</Heading>
                        <Subtitle darkText={darkText}>{description}</Subtitle>
                        {/* <BtnWrap>
                            <Button to='/'
                            smooth={true}
                            duration={500}
                            spy={true} 
                            exact="true" 
                            offset={-28}
                            primary={primary ? 1 : 0}
                            dark={dark ? 1 : 0}
                            dark2={dark2 ? 1 : 0}
                            >{buttonLabel}</Button>
                        </BtnWrap> */}
                        </TextWrapper>
                      </Column1>
                      <Column2>
                        <ImgWrap>
                            <Img src={img} alt={alt} />
                        </ImgWrap>
                      </Column2>
                  </InfoRow>
              </InfoWrapper>
          </InfoContainer>  
        </>
    )
}

export default InfoSection
