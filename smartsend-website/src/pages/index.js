import React, { useState } from 'react'
import SideBar from '../components/sidebar'
import NavBar from '../components/navbar'
import HeroSection from '../components/herosection'
import InfoSection from '../components/infosection'
import { homeObjOne, homeObjTwo, homeObjThree } from '../components/infosection/data'
import Services from '../components/services'
import Footer from '../components/footer'
import Form from '../components/registerform/Form'

const Home = () => {
    const [isOpen, setIsOpen] = useState(false)

    const toggle = () => {
        setIsOpen(!isOpen)
    }

    return (
        <>
            <SideBar isOpen={isOpen} toggle={toggle}/>
            <NavBar toggle={toggle}/>
            <HeroSection />
            <InfoSection {...homeObjOne} />
            <InfoSection {...homeObjTwo} />
            <Services />
            {/* <InfoSection {...homeObjThree} /> */}
            <Form />
            <Footer />
        </>
    )
}

export default Home
