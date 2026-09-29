import React, { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { z, ZodIssue } from 'zod'
import { toast } from 'react-toastify'
import i18next from '../../lang'

import LoginButton from '../reusableComponents/LoginButton'
import Checkbox from '../reusableComponents/inputs/Checkbox'
import TextInput from '../reusableComponents/inputs/TextInput'
import { useRegistrationMutation } from '../../api/user'

const registrationSchema = z
  .object({
    firstName: z.string({
      required_error: i18next.t('navigation:field_is_required'),
    }),
    lastName: z.string({
      required_error: i18next.t('navigation:field_is_required'),
    }),
    email: z
      .string({
        required_error: i18next.t('navigation:field_is_required'),
      })
      .email({
        message: i18next.t('navigation:bad_email_format'),
      }),
    password: z.string({
      required_error: i18next.t('navigation:field_is_required'),
    }),
    repeatedPassword: z.string({
      required_error: i18next.t('navigation:field_is_required'),
    }),
    terms: z.literal<boolean>(true, {
      errorMap: () => ({
        message: i18next.t('navigation:accepted_terms_required'),
      }),
    }),
  })
  .refine((data) => data.password === data.repeatedPassword, {
    message: i18next.t('navigation:bad_repeated_password'),
    path: ['repeatedPassword'],
  })

type TRegistrationSchema = z.infer<typeof registrationSchema>

// const validate = (values: TRegistrationSchema) => {
//   const refinedObject = registrationSchema.refine(
//     (obj) => obj.password === obj.repeatedPassword,
//     {
//       message: i18next.t('navigation:bad_repeated_password'),
//       path: ['registrationRepeatedPassword'],
//     }
//   )
//   return refinedObject.safeParse(values)
// }

// const handleSubmit = (values: TRegistrationSchema) => {
//   const validationResult = validate(values)
//   if (!validationResult.success) {
//     validationResult.error.errors.map((e) => toast.error(e.message))
//   }
//   if (validationResult.success) {
//     // send request
//     toast.success('Vše v pořádku')
//     // console.log(validationResult.data)
//   }
// }

const initialValues = {
  email: '',
  firstName: '',
  lastName: '',
  password: '',
  repeatedPassword: '',
  terms: false,
}

const Registration: FC = () => {
  const { t } = useTranslation('navigation')
  const [errors, setErrors] = useState<ZodIssue[]>([])
  const [values, setValues] = useState<TRegistrationSchema>(initialValues)
  const { mutateAsync: doRegistration, status: registrationStatus } =
    useRegistrationMutation()
  // const validateField = (field: keyof TRegistrationSchema, value: string) => {
  //   const parsedResult = registrationSchema
  //     .pick({ [field]: true })
  //     .safeParse({ [field]: value })
  //   return parsedResult.success ? '' : parsedResult.error.errors[0].message
  // }

  const handleSubmit = () => {
    const validation = registrationSchema.safeParse(values)

    if (validation.success) {
      doRegistration({
        firstName: values.firstName,
        lastName: values.lastName,
        email: values.email,
        password: values.password,
      })
        .then(() => {
          toast.success(t('registration_successful'))
          setValues(initialValues)
        })
        .catch(() => {})
    } else {
      if (!values.terms) {
        toast.error(t('accepted_terms_required'))
      }
      setErrors(validation.error.errors)
    }
  }

  return (
    <div className="flex h-full w-full flex-col items-start gap-2 p-8 pt-8">
      <TextInput
        id="firstName"
        placeholder={t('name')}
        value={values.firstName}
        error={errors.find((e) => e.path[0] === 'firstName')?.message}
        onChange={(value) => {
          setValues((prevState) => ({
            ...prevState,
            firstName: value,
          }))
        }}
        className={`w-full outline-superlightgray transition ${
          values.firstName ? 'bg-white' : 'bg-opacity-30 caret-white'
        } font-bold placeholder-white ${
          errors.find((e) => e.path[0] === 'firstName') ? 'mb-0.5' : 'mb-6'
        }`}
      />
      <TextInput
        id="lastName"
        placeholder={t('surname')}
        value={values.lastName}
        error={errors.find((e) => e.path[0] === 'lastName')?.message}
        onChange={(value) => {
          setValues((prevState) => ({
            ...prevState,
            lastName: value,
          }))
        }}
        className={`w-full outline-superlightgray transition ${
          values.lastName ? 'bg-white' : 'bg-opacity-30 caret-white'
        } font-bold placeholder-white ${
          errors.find((e) => e.path[0] === 'lastName') ? 'mb-0.5' : 'mb-6'
        }`}
      />
      <TextInput
        id="email"
        placeholder={t('email')}
        value={values.email}
        error={errors.find((e) => e.path[0] === 'email')?.message}
        onChange={(value) => {
          setValues((prevState) => ({
            ...prevState,
            email: value,
          }))
        }}
        className={`w-full outline-superlightgray transition ${
          values.email ? 'bg-white' : 'bg-opacity-30 caret-white'
        } font-bold placeholder-white ${
          errors.find((e) => e.path[0] === 'email') ? 'mb-0.5' : 'mb-6'
        }`}
      />
      <TextInput
        type="password"
        id="password"
        placeholder={t('password')}
        value={values.password}
        error={errors.find((e) => e.path[0] === 'password')?.message}
        onChange={(value) => {
          setValues((prevState) => ({
            ...prevState,
            password: value,
          }))
        }}
        className={`w-full outline-superlightgray transition ${
          values.password ? 'bg-white' : 'bg-opacity-30 caret-white'
        } font-bold placeholder-white ${
          errors.find((e) => e.path[0] === 'password') ? 'mb-0.5' : 'mb-6'
        }`}
      />
      <TextInput
        type="password"
        id="repeatedPassword"
        placeholder={t('repeated_password')}
        value={values.repeatedPassword}
        error={errors.find((e) => e.path[0] === 'repeatedPassword')?.message}
        onChange={(value) => {
          setValues((prevState) => ({
            ...prevState,
            repeatedPassword: value,
          }))
        }}
        className={`w-full outline-superlightgray transition ${
          values.repeatedPassword ? 'bg-white' : 'bg-opacity-30 caret-white'
        } font-bold placeholder-white ${
          errors.find((e) => e.path[0] === 'repeatedPassword')
            ? 'mb-0.5'
            : 'mb-6'
        }`}
      />
      {/* <Field */}
      {/*  name="registrationName" */}
      {/*  validate={(e) => validateField('registrationName', e)} */}
      {/*  render={({ input, meta }) => ( */}
      {/*    <div className="w-full"> */}
      {/*      <Input */}
      {/*        // eslint-disable-next-line react/jsx-props-no-spreading */}
      {/*        {...input} */}
      {/*        id="registrationName" */}
      {/*        placeholder={t('name')} */}
      {/*        value={input.value} */}
      {/*        className={`w-full outline-superlightgray transition ${ */}
      {/*          input.value ? 'bg-white ' : 'bg-opacity-30 caret-white' */}
      {/*        } font-bold placeholder-white ${ */}
      {/*          (meta.visited || submitClicked) && meta.error */}
      {/*            ? 'mb-0.5' */}
      {/*            : 'mb-6' */}
      {/*        }`} */}
      {/*      /> */}
      {/*      {(meta.visited || submitClicked) && meta.error ? ( */}
      {/*        <span className="pl-2 text-white">{meta.error}</span> */}
      {/*      ) : null} */}
      {/*    </div> */}
      {/*  )} */}
      {/* /> */}
      {/* <Field */}
      {/*  name="registrationSurname" */}
      {/*  validate={(e) => validateField('registrationSurname', e)} */}
      {/*  render={({ input, meta }) => ( */}
      {/*    <div className="w-full"> */}
      {/*      <Input */}
      {/*        // eslint-disable-next-line react/jsx-props-no-spreading */}
      {/*        {...input} */}
      {/*        id="registrationSurname" */}
      {/*        placeholder={t('surname')} */}
      {/*        value={input.value} */}
      {/*        className={`w-full outline-superlightgray transition ${ */}
      {/*          input.value ? 'bg-white ' : 'bg-opacity-30 caret-white' */}
      {/*        } font-bold placeholder-white ${ */}
      {/*          (meta.visited || submitClicked) && meta.error */}
      {/*            ? 'mb-0.5' */}
      {/*            : 'mb-6' */}
      {/*        }`} */}
      {/*      /> */}
      {/*      {(meta.visited || submitClicked) && meta.error ? ( */}
      {/*        <span className="pl-2 text-white">{meta.error}</span> */}
      {/*      ) : null} */}
      {/*    </div> */}
      {/*  )} */}
      {/* /> */}
      {/* <Field */}
      {/*  name="registrationEmail" */}
      {/*  validate={(e) => validateField('registrationEmail', e)} */}
      {/*  render={({ input, meta }) => ( */}
      {/*    <div className="w-full"> */}
      {/*      <Input */}
      {/*        // eslint-disable-next-line react/jsx-props-no-spreading */}
      {/*        {...input} */}
      {/*        id="registrationEmail" */}
      {/*        placeholder={t('email')} */}
      {/*        value={input.value} */}
      {/*        className={`w-full outline-superlightgray transition ${ */}
      {/*          input.value ? 'bg-white ' : 'bg-opacity-30 caret-white' */}
      {/*        } font-bold placeholder-white ${ */}
      {/*          (meta.visited || submitClicked) && meta.error */}
      {/*            ? 'mb-0.5' */}
      {/*            : 'mb-6' */}
      {/*        }`} */}
      {/*      /> */}
      {/*      {(meta.visited || submitClicked) && meta.error ? ( */}
      {/*        <span className="pl-2 text-white">{meta.error}</span> */}
      {/*      ) : null} */}
      {/*    </div> */}
      {/*  )} */}
      {/* /> */}
      {/* <Field */}
      {/*  name="registrationPassword" */}
      {/*  validate={(e) => validateField('registrationPassword', e)} */}
      {/*  render={({ input, meta }) => ( */}
      {/*    <div className="w-full"> */}
      {/*      <Input */}
      {/*        // eslint-disable-next-line react/jsx-props-no-spreading */}
      {/*        {...input} */}
      {/*        password */}
      {/*        id="registrationPassword" */}
      {/*        type="password" */}
      {/*        placeholder={t('password')} */}
      {/*        value={input.value} */}
      {/*        className={`w-full outline-superlightgray transition ${ */}
      {/*          input.value ? 'bg-white ' : 'bg-opacity-30 caret-white' */}
      {/*        } font-bold placeholder-white ${ */}
      {/*          (meta.visited || submitClicked) && meta.error */}
      {/*            ? 'mb-0.5' */}
      {/*            : 'mb-6' */}
      {/*        }`} */}
      {/*      /> */}
      {/*      {(meta.visited || submitClicked) && meta.error ? ( */}
      {/*        <span className="pl-2 text-white">{meta.error}</span> */}
      {/*      ) : null} */}
      {/*    </div> */}
      {/*  )} */}
      {/* /> */}
      {/* <Field */}
      {/*  name="registrationRepeatedPassword" */}
      {/*  validate={(e) => validateField('registrationRepeatedPassword', e)} */}
      {/*  render={({ input, meta }) => ( */}
      {/*    <div className="w-full"> */}
      {/*      <Input */}
      {/*        // eslint-disable-next-line react/jsx-props-no-spreading */}
      {/*        {...input} */}
      {/*        password */}
      {/*        id="registrationRepeatedPassword" */}
      {/*        type="password" */}
      {/*        placeholder={t('repeated_password')} */}
      {/*        value={input.value} */}
      {/*        className={`w-full outline-superlightgray transition ${ */}
      {/*          input.value ? 'bg-white ' : 'bg-opacity-30 caret-white' */}
      {/*        } font-bold placeholder-white ${ */}
      {/*          (meta.visited || submitClicked) && meta.error */}
      {/*            ? 'mb-0.5' */}
      {/*            : 'mb-6' */}
      {/*        }`} */}
      {/*      /> */}
      {/*      {(meta.visited || submitClicked) && meta.error ? ( */}
      {/*        <span className="pl-2 text-white">{meta.error}</span> */}
      {/*      ) : null} */}
      {/*    </div> */}
      {/*  )} */}
      {/* /> */}
      <Checkbox
        checked={values.terms}
        id="terms"
        name={t('accept_terms')}
        onChange={(value) => {
          setValues((prevState) => ({
            ...prevState,
            terms: value,
          }))
        }}
        link={t('accept_terms_link')}
        linkName={t('accept_terms_link_name')}
        externalLink
        showName
      />
      {/* <Field */}
      {/*  name="checkbox-registrationCheckboxTerms" */}
      {/*  type="checkbox" */}
      {/*  validate={(e) => */}
      {/*    validateField('checkbox-registrationCheckboxTerms', e) */}
      {/*  } */}
      {/*  render={({ input, meta }) => ( */}
      {/*    <div */}
      {/*      className={`w-full ${ */}
      {/*        (meta.touched || submitClicked) && meta.error */}
      {/*          ? 'mb-0.5' */}
      {/*          : 'mb-6' */}
      {/*      }`} */}
      {/*    > */}
      {/*      <Checkbox */}
      {/*        // eslint-disable-next-line react/jsx-props-no-spreading */}
      {/*        {...input} */}
      {/*        id="registrationCheckboxTerms" */}
      {/*        name={t('accept_terms')} */}
      {/*        link="/terms" */}
      {/*        linkName={t('accept_terms_link_name')} */}
      {/*        className="text-white" */}
      {/*        wrapperClassName="my-0" */}
      {/*        showName */}
      {/*      /> */}
      {/*      {(meta.touched || submitClicked) && meta.error ? ( */}
      {/*        <span className="text-white">{meta.error}</span> */}
      {/*      ) : null} */}
      {/*    </div> */}
      {/*  )} */}
      {/* /> */}
      <LoginButton
        onClick={() => handleSubmit()}
        loading={registrationStatus === 'pending'}
      >
        {t('register')}
      </LoginButton>
    </div>
  )
}

export default Registration
