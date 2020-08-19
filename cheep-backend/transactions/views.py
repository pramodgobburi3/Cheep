from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from oauthlib.common import generate_token
from oauth2_provider.models import AccessToken, Application, RefreshToken
from django.utils import timezone
from django.http import JsonResponse
from plaid_api.api import *
from users.models import UserProfile
import json
import after_response
from .models import *
from accounts.models import *
from categories.models import *
from django.utils.dateparse import parse_date
from budgets.models import *
from jars.models import *
from budgets.serializers import *
from jars.serializers import *
from accounts.serializers import *
from datetime import datetime

def get_transaction_category(transaction):
    categories = transaction["category"]

    print(categories)

    for i in range(len(categories)-1, -1, -1):
        print(i)
        category = categories[i]
        print(category)
        if Category.objects.filter(plaid_category_name=category).exists():
            return Category.objects.get(plaid_category_name=category)

    return Category.objects.get(plaid_category_name="miscellaneous")

@after_response.enable
def add_transactions_to_table(transactions):
    for transaction in transactions:
        if Transactions.objects.filter(plaid_transaction_id=transaction["transaction_id"]).exists():
            pass
        else:
            account = Accounts.objects.get(accountId=transaction["account_id"])
            date = parse_date(transaction["date"])
            category = get_transaction_category(transaction)
            t = Transactions.objects.create(account_id=account.id, plaid_transaction_id=transaction["transaction_id"],
                                            transaction_name=transaction["name"], transaction_datetime=date,
                                            transaction_amount=transaction["amount"], transaction_category=str(category.id))


# Create your views here.
@csrf_exempt
def fetch_transactions(request):
    if request.method == "POST":
        body = json.loads(request.body)
        if body["access_token"] is None:
            return JsonResponse({"status": "No access token provided"}, safe=False)
        else:
            try:
                access_token = AccessToken.objects.get(token=body["access_token"],
                                                       expires__gt=timezone.now())
            except:
                try:
                    access_token = AccessToken.objects.get(token=body["access_token"])
                except:
                    return JsonResponse({"status": "Invalid token"})
                else:
                    return JsonResponse({"status": "Access token has expired. Request a new one."})
            else:
                plaid_access_token = body["plaid_access_token"]
                account_ids = body["account_ids"]
                start_date = body["start_date"]
                stop_date = body["end_date"]
                user = access_token.user
                transactions = get_transaction(plaid_access_token, start_date, stop_date, account_ids)
                add_transactions_to_table.after_response(transactions["transactions"]["transactions"])
                return JsonResponse({"status": "Successful", "transactions":transactions["transactions"]["transactions"]}, safe=False)

@csrf_exempt
def fetch_all_financial_data(request):
    if request.method == "POST":
        if request.POST.get("access_token") is None:
            return JsonResponse({"status": "No access token provided"}, safe=False)
        else:
            try:
                access_token = AccessToken.objects.get(token=request.POST.get("access_token"),
                                                       expires__gt=timezone.now())
            except:
                try:
                    access_token = AccessToken.objects.get(token=request.POST.get("access_token"))
                except:
                    return JsonResponse({"status": "Invalid token"})
                else:
                    return JsonResponse({"status": "Access token has expired. Request a new one."})
            else:
                user = access_token.user
                plaid_access_token = request.POST.get("plaid_access_token")
                print(plaid_access_token)
                accounts = Accounts.objects.filter(user_id=user.id)
                acc_ids = json.loads(json.dumps(AccountIdSerializer(accounts, many=True).data))
                account_ids = []
                for i in acc_ids:
                    account_ids.append(i["accountId"])
                today = datetime.today()
                datem = datetime(today.year, today.month-2, 1)
                today_f = today.strftime("%Y-%m-%d")
                datem_f = datem.strftime("%Y-%m-%d")
                transactions = get_transaction(plaid_access_token, datem_f, today_f, account_ids)
                print(transactions)
                budgets = Budgets.objects.filter(user_id=user.id)
                budgets_json = BudgetSerializer(budgets, many=True).data
                jars = Jars.objects.filter(user_id=user.id)
                jars_json = JarsSerializer(jars, many=True).data
                add_transactions_to_table.after_response(transactions["transactions"]["transactions"])
                return JsonResponse({"status": "Successful", "transactions": transactions["transactions"]["transactions"],
                                     "budgets": budgets_json, "jars": jars_json}, safe=False)



