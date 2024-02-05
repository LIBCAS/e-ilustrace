import { FC, useCallback, useEffect, useState, useRef } from 'react'
import './multiRangeSlider.css'

type Props = {
  name: string
  min: number
  initMinValue: number
  max: number
  initMaxValue: number
  onChange: ({ min, max }: { min: number; max: number }) => void
}

const MultiRangeSlider: FC<Props> = ({
  name,
  min,
  initMinValue,
  max,
  initMaxValue,
  onChange,
}) => {
  const [minVal, setMinVal] = useState(initMinValue)
  const [maxVal, setMaxVal] = useState(initMaxValue)
  const minValRef = useRef(min)
  const maxValRef = useRef(max)
  const range = useRef<HTMLDivElement>(null)
  const [dragging, setDragging] = useState(false)

  // Convert to percentage
  const getPercent = useCallback(
    (value: number) => Math.floor(((value - min) / (max - min)) * 100),
    [min, max]
  )

  // Set width of the range to decrease from the left side
  useEffect(() => {
    const minPercent = getPercent(minVal)
    const maxPercent = getPercent(maxValRef.current)

    if (range.current) {
      range.current.style.left = `${minPercent}%`
      range.current.style.width = `${maxPercent - minPercent}%`
    }
  }, [minVal, getPercent])

  // Set width of the range to decrease from the right side
  useEffect(() => {
    const minPercent = getPercent(minValRef.current)
    const maxPercent = getPercent(maxVal)

    if (range.current) {
      range.current.style.width = `${maxPercent - minPercent}%`
    }
  }, [maxVal, getPercent])

  // Get min and max values when their store changes
  useEffect(() => {
    if (!dragging) onChange({ min: minVal, max: maxVal })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dragging, minVal, maxVal])

  return (
    <div className="relative mb-8 w-full">
      <p className="mb-4 font-bold">{name}</p>
      <input
        type="range"
        min={min}
        max={max}
        value={minVal}
        onChange={(event) => {
          const value = Math.min(Number(event.target.value), maxVal - 1)
          setMinVal(value)
          minValRef.current = value
        }}
        onMouseDown={() => setDragging(true)}
        onMouseUp={() => setDragging(false)}
        className="thumb thumb--left"
        style={{ zIndex: minVal > max - 100 ? '5' : undefined }}
      />
      <input
        type="range"
        min={min}
        max={max}
        value={maxVal}
        onChange={(event) => {
          const value = Math.max(Number(event.target.value), minVal + 1)
          setMaxVal(value)
          maxValRef.current = value
        }}
        onMouseDown={() => setDragging(true)}
        onMouseUp={() => setDragging(false)}
        className="thumb thumb--right"
      />

      <div className="slider">
        <div className="slider__track" />
        <div ref={range} className="slider__range" />
        <div className="slider__left-value">{minVal}</div>
        <div className="slider__right-value">{maxVal}</div>
      </div>
    </div>
  )
}

export default MultiRangeSlider
