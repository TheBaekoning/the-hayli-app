<svelte:options runes={true} />
<script lang="ts">
	import { goto } from '$app/navigation';
	import ButtonLink from '$components/button-link/button-link.svelte';
	import Input from '$components/inputs/input.svelte';
	import { EButtonType } from '$lib';
	import { apiFetch, csrfToken } from '$lib/api-fetch';
	import { onMount } from 'svelte';

	const required = true;
	let email = $state('');
	let password = $state('');
	let error: null | string = $state(null);
	let sending = $state(false);
	const headers = new Headers({
		'Content-Type': 'application/json'
	});

	function registerUser(e: Event){
		e.preventDefault();
		sending = true;
		apiFetch({
			url: '/login/register', 
			options: {
				method: 'POST',
				headers,
				body: JSON.stringify({
					email,
					password
				})
			}
		}).then(resp => {
			if(resp.ok){
				goto('/sign-up/verify');
			}
			else {
				resp.text().then(text => console.error(text));
			}
		}, err => {
			error = err.message;
		}).finally(() => {
			sending = false;
		});
	}
</script>

<main class="index">
	<header>
		<div class="breadcrumbs"><a href="/">Back</a></div>
		<h1>Create an account.</h1>
	</header>
	{#if error}
		<p class="error">{error}</p>
	{/if}
	<form action="" method="post" onsubmit={registerUser} class:sending>
		<Input type="email" {required} bind:value={email} />
		<Input type="password" {required} bind:value={password} />
		<div class="signup">
			<ButtonLink type={EButtonType.submit}>Sign Up</ButtonLink>
		</div>
	</form>
	<p>Have a verification code you need to enter? <a href="/sign-up/verify">Verify your account.</a></p>
</main>
<style lang="scss">
	main{
		height: 100%;
	}
	form{
		height: 100%;
		.signup{
			margin-top: auto;
		}
		&.sending{
			opacity: 0.5;
			pointer-events: none;
		}
	}
	@media (min-width: 1000px){
		main{
			max-width: 40%;
		}
	}
</style>