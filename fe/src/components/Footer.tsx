import React from 'react'

import Logo from '../assets/icons/logo.svg?react'
import logo_knav from '../assets/images/logo_knav.png'
import logo_iq from '../assets/images/logo_iq.png'
import logo_nk from '../assets/images/logo_nk.png'
import logo_dhox from '../assets/images/logo_dhox.png'
import logo_vgg from '../assets/images/logo_vgg.png'
import UpArrow from '../assets/icons/up_arrow.svg?react'

const Footer = () => {
  return (
    <footer className="bottom-0 left-0 flex h-20 w-full items-center justify-between bg-footergray px-5 text-sm text-black">
      <div className="wrapper flex justify-between">
        <div className="flex items-center">
          <span className="flex items-center font-normal text-gray">
            &copy; 2021
          </span>
          <div className="flex h-full w-full max-w-[127px] items-center justify-between">
            <a
              aria-label="E-Ilustrace logo"
              className="transition-all duration-200 hover:scale-[1.05]"
              href="https://e-ilustrace.cz/"
              target="_blank"
              rel="noreferrer"
            >
              <Logo className="scale-75" />
            </a>
          </div>
        </div>
        <div className="flex items-center gap-16 opacity-70">
          <a
            className="transition-all duration-200 hover:scale-[1.05]"
            href="https://www.lib.cas.cz/"
            target="_blank"
            rel="noreferrer"
          >
            <img src={logo_knav} alt="logo_knav" />
          </a>
          <a
            className="transition-all duration-200 hover:scale-[1.05]"
            href="https://inqool.cz/"
            target="_blank"
            rel="noreferrer"
          >
            <img src={logo_iq} alt="logo_iq" />
          </a>
          <a
            className="transition-all duration-200 hover:scale-[1.05]"
            href="https://www.nkp.cz/"
            target="_blank"
            rel="noreferrer"
          >
            <img src={logo_nk} alt="logo_nk" />
          </a>
          <a
            className="transition-all duration-200 hover:scale-[1.05]"
            href="https://digital.humanities.ox.ac.uk/#/"
            target="_blank"
            rel="noreferrer"
          >
            <img src={logo_dhox} alt="logo_dhox" />
          </a>
          <a
            className="transition-all duration-200 hover:scale-[1.05]"
            href="https://www.robots.ox.ac.uk/~vgg/"
            target="_blank"
            rel="noreferrer"
          >
            <img src={logo_vgg} alt="logo_vgg" />
          </a>
          <UpArrow
            className="cursor-pointer text-red"
            onClick={() => {
              window.scrollTo(0, 0)
            }}
          />
        </div>
      </div>
    </footer>
  )
}

export default Footer
