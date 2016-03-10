update SITE set PASSWORD_VARIANT='Password' where PASSWORD_VARIANT is null;
update SITE set PASSWORD_VARIANT='Answer' where PASSWORD_TYPE='GeneratedPhrase'