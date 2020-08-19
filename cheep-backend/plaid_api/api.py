from plaid import Client
import plaid
import datetime

clientId = 'CLIENT_ID'
secret = 'SECRET'
publicKey = 'PUBLIC_KEY'
environment = 'sandbox'

# Initialize client
client = Client(client_id=clientId, secret=secret,
                public_key=publicKey, environment=environment, api_version='2018-05-22')


def get_access_token(public_token):
    response = client.Item.public_token.exchange(public_token)
    access_token = response['access_token']
    return access_token


def get_auth(access_token):
    try:
        auth_response = client.Auth.get(access_token)
    except plaid.errors.PlaidError as e:
        return {'error': {'display_message': e.display_message, 'error_code': e.code, 'error_type': e.type}}
    return {'error': None, 'auth': auth_response}


def get_transaction(access_token, start_date, end_date, account_ids):
    start_date = start_date.format(datetime.datetime.now() + datetime.timedelta(-30))
    end_date = end_date.format(datetime.datetime.now())
    print(start_date, end_date, account_ids)
    try:
        transactions_response = client.Transactions.get(access_token, start_date, end_date, account_ids=account_ids)
    except plaid.errors.PlaidError as e:
        return format_error(e)
    return {'error': None, 'transactions': transactions_response}


def get_identity(access_token):
    try:
        identity_response = client.Identity.get(access_token)
    except plaid.errors.PlaidError as e:
        return {'error': {'display_message': e.display_message, 'error_code': e.code, 'error_type': e.type}}
    return {'error': None, 'identity': identity_response}


def get_balance(access_token):
    try:
        balance_response = client.Accounts.balance.get(access_token)
    except plaid.errors.PlaidError as e:
        return {'error': {'display_message': e.display_message, 'error_code': e.code, 'error_type': e.type}}
    return {'error': None, 'balance': balance_response}


def get_accounts(access_token):
    try:
        accounts_response = client.Accounts.get(access_token)
    except plaid.errors.PlaidError as e:
        return {'error': {'display_message': e.display_message, 'error_code': e.code, 'error_type': e.type}}
    return {'error': None, 'accounts': accounts_response}


def format_error(e):
    return {'error': {'display_message': e.display_message, 'error_code': e.code, 'error_type': e.type, 'error_message': e.message } }

