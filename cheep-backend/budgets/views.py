from django.shortcuts import render
from django.http import JsonResponse
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt
from oauthlib.common import generate_token
from oauth2_provider.models import AccessToken, Application, RefreshToken
from .models import *
import datetime
from django.utils import timezone
from plaid_api.api import *
from .serializers import *
import json

# Create your views here.
@csrf_exempt
def get_budgets(request):
    if request.method == 'POST':
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
                budget = Budgets.objects.filter(user_id=user.id)
                budgets = BudgetSerializer(budget, many=True).data
                return JsonResponse({"status": "success", "budgets": budgets}, safe=False)

@csrf_exempt
def add_budget(request):
    if request.method == 'POST':
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
                budget_name = request.POST.get("budget_name")
                budget_limit = request.POST.get("budget_limit")
                budget_spent = request.POST.get("budget_spent")
                budget_color = request.POST.get("budget_color")
                last_transaction = request.POST.get("last_transaction")

                budget = Budgets.objects.create(user=user, name=budget_name, limit=budget_limit, spent=budget_spent,
                                                color=budget_color, last_transaction=last_transaction)

                return JsonResponse({"status": "success"}, safe=False)


@csrf_exempt
def update_budget(request):
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
                budget_id = request.POST.get("id")
                budget_name = request.POST.get("budget_name")
                budget_limit = request.POST.get("budget_limit")
                budget_spent = request.POST.get("budget_spent")
                budget_color = request.POST.get("budget_color")
                last_transaction = request.POST.get("last_transaction")

                budget = Budgets.objects.get(id=budget_id)
                budget.name = budget_name
                budget.limit = budget_limit
                budget.spent = budget_spent
                budget.color = budget_color
                budget.last_transaction = last_transaction
                budget.save()

                return JsonResponse({"status": "success", "budget": budget}, safe=False)

