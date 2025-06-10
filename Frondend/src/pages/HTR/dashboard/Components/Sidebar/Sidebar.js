import React from 'react'
import styles from './sidebar.module.css'
import ButtonComponent from '@/components/HTR/dashboard/buttons/ButtonComponent'
import Image from 'next/image'
import MedilitLogo from '@/Assets/HTR/Sidebar/MedLogo.png'
import InfiniteLogo from '@/Assets/HTR/Sidebar/InfiniteLogo.png'

function Sidebar({ data }) {


    return (
        <div className={styles.sideBarWrapper} >
            <div className={styles.buttonWrapper}>
                {
                    data.map((btnData, index) => (
                        <ButtonComponent data={btnData} key={index} />
                    ))
                }
            </div>
            <div className={styles.imgWrapper} >
                <Image src={InfiniteLogo} className={styles.sideBarImg} />
                <Image src={MedilitLogo} className={styles.sideBarImg} />
            </div>
        </div>
    )
}

export default Sidebar