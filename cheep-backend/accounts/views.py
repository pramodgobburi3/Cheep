from django.shortcuts import render
from django.http import JsonResponse
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt
from oauthlib.common import generate_token
from oauth2_provider.models import AccessToken, Application, RefreshToken
from .models import *
import datetime
from django.utils import timezone
from .models import Accounts
from .serializers import AccountSerializer
from plaid_api.api import *

# Create your views here.
@csrf_exempt
def create_account(request):
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
                public_key = request.POST.get("public_key")
                account_id = request.POST.get("account_id")
                account_name = request.POST.get("account_name")

                access_token = get_access_token(public_key)

                account = Accounts.objects.create(user_id=user.id, accessToken=access_token, accountId=account_id, name=account_name)

                return JsonResponse({"status": "success", "account": AccountSerializer(account).data}, safe=False)
