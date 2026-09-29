import { CSSProperties, FC, useEffect, useState } from 'react'
import { Slider, Rail, Handles, Tracks } from 'react-compound-slider'
import { SliderRail, Handle, Track } from './components'

type TProps = {
  fromValue: number
  toValue: number
  bothValuesChange: (values: readonly number[]) => void
  bottomLimit: number
  topLimit: number
}

const RangeSlider: FC<TProps> = ({
  fromValue,
  toValue,
  bothValuesChange,
  bottomLimit,
  topLimit,
}) => {
  const [values, setValues] = useState({ from: fromValue, to: toValue })

  useEffect(() => {
    setValues({ from: fromValue, to: toValue })
  }, [fromValue, toValue])

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
      <div className="flex items-center justify-between gap-4 text-gray">
        <span>{values.from}</span>
        <span>{values.to}</span>
      </div>
    </div>
  )
}

export default RangeSlider
