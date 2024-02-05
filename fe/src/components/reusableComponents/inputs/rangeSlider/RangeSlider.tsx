import { CSSProperties, FC, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Slider, Rail, Handles, Tracks } from 'react-compound-slider'
import { SliderRail, Handle, Track } from './components'
import NumberInput from '../NumberInput'

type TProps = {
  fromValue: number
  toValue: number
  fromValueChange: (value: number) => void
  toValueChange: (value: number) => void
  bothValuesChange: (values: readonly number[]) => void
  bottomLimit: number
  topLimit: number
}

const RangeSlider: FC<TProps> = ({
  fromValue,
  toValue,
  fromValueChange,
  toValueChange,
  bothValuesChange,
  bottomLimit,
  topLimit,
}) => {
  const { t } = useTranslation()
  const [values, setValues] = useState({ from: fromValue, to: toValue })

  useEffect(() => {
    setValues({ from: fromValue, to: toValue })
  }, [fromValue, toValue])

  const handleFromValueChange = (number: string) => {
    setValues((prevState) => ({ from: Number(number), to: prevState.to }))
    fromValueChange(Number(number))
  }

  const handleToValueChange = (number: string) => {
    setValues((prevState) => ({ from: prevState.from, to: Number(number) }))
    toValueChange(Number(number))
  }

  const handleChange = (nextState: readonly number[]) => {
    bothValuesChange(nextState)
    setValues({ from: nextState[0], to: nextState[1] })
  }

  const handleUpdate = (nextState: readonly number[]) => {
    setValues({ from: nextState[0], to: nextState[1] })
  }

  const sliderStyle = {
    position: 'relative',
    width: '100%',
    touchAction: 'none',
    margin: '1rem auto',
    height: '0.3rem',
  } as CSSProperties

  return (
    <div className="flex flex-col gap-2">
      <div>
        <Slider
          mode={3}
          step={1}
          domain={[bottomLimit, topLimit]}
          rootStyle={sliderStyle}
          onChange={handleChange}
          onUpdate={handleUpdate}
          values={[values.from, values.to]}
        >
          <Rail>
            {({ getRailProps }) => <SliderRail getRailProps={getRailProps} />}
          </Rail>
          <Handles>
            {({ handles, getHandleProps }) => (
              <div className="slider-handles">
                {handles.map((handle) => (
                  <Handle
                    key={handle.id}
                    handle={handle}
                    domain={[bottomLimit, topLimit]}
                    getHandleProps={getHandleProps}
                    label=""
                  />
                ))}
              </div>
            )}
          </Handles>
          <Tracks left={false} right={false}>
            {({ tracks, getTrackProps }) => (
              <div className="slider-tracks">
                {tracks.map(({ id, source, target }) => (
                  <Track
                    key={id}
                    source={source}
                    target={target}
                    getTrackProps={getTrackProps}
                  />
                ))}
              </div>
            )}
          </Tracks>
        </Slider>
      </div>
      <div className="flex items-center gap-4">
        <NumberInput
          id="year-from"
          className="border-2 border-[#E9ECEF] bg-white"
          onChange={(newValue) => handleFromValueChange(newValue)}
          value={values.from}
          min={bottomLimit}
          max={topLimit}
          placeholder={t('search:from')}
        />
        -
        <NumberInput
          id="year-tp"
          className="border-2 border-[#E9ECEF] bg-white"
          onChange={(newValue) => handleToValueChange(newValue)}
          value={values.to}
          min={bottomLimit}
          max={topLimit}
          placeholder={t('search:to')}
        />
      </div>
    </div>
  )
}

export default RangeSlider
